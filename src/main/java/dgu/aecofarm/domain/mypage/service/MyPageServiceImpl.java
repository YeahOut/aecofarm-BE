package dgu.aecofarm.domain.mypage.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import dgu.aecofarm.dto.mypage.*;
import dgu.aecofarm.entity.*;
import dgu.aecofarm.exception.InvalidUserIdException;
import dgu.aecofarm.repository.AlarmRepository;
import dgu.aecofarm.repository.ContractRepository;
import dgu.aecofarm.repository.LoveRepository;
import dgu.aecofarm.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MyPageServiceImpl implements MyPageService {

    private final MemberRepository memberRepository;
    private final ContractRepository contractRepository;
    private final LoveRepository loveRepository;
    private final AlarmRepository alarmRepository;
    private final ObjectMapper objectMapper;

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public MyPageResponseDTO getMyPage(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new InvalidUserIdException("유효한 사용자 ID가 아닙니다."));

        ProfileDTO profile = ProfileDTO.builder()
                .userName(member.getUserName())
                .email(member.getEmail())
                .image(member.getImage())
                .point(member.getPoint())
                .build();

        List<Object> rawRecentList;
        List<Long> recentContractIds;
        try {
            rawRecentList = member.getRecent() == null ? List.of() : objectMapper.readValue(member.getRecent(), List.class);
            recentContractIds = rawRecentList.stream()
                    .map(id -> {
                        if (id instanceof Integer) {
                            return ((Integer) id).longValue();
                        } else if (id instanceof Long) {
                            return (Long) id;
                        } else {
                            throw new IllegalArgumentException("유효하지 않은 ID 타입입니다.");
                        }
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("최근 본 물품을 로드하는데 실패했습니다.", e);
        }

        // 최근 본 물품을 역순으로 가져오기
        List<Contract> recentContracts = new ArrayList<>();
        for (int i = recentContractIds.size() - 1; i >= 0; i--) {
            Long contractId = recentContractIds.get(i);
            contractRepository.findById(contractId)
                    .filter(contract -> contract.getStatus() == Status.NONE)
                    .ifPresent(recentContracts::add);
        }

        List<HistoryDTO> historyList = recentContracts.stream().map(contract -> {
            boolean likeStatus = loveRepository.existsByItemAndMember(contract.getItem(), member);

            return HistoryDTO.builder()
                    .contractId(contract.getContractId())
                    .itemName(contract.getItem().getItemName())
                    .itemImage(contract.getItem().getItemImage())
                    .time(contract.getItem().getTime())
                    .price(contract.getItem().getPrice())
                    .likeStatus(likeStatus)
                    .build();
        }).collect(Collectors.toList());

        return MyPageResponseDTO.builder()
                .profile(profile)
                .history(historyList)
                .build();
    }

    public void updateProfile(Long memberId, UpdateProfileDTO updateProfileDTO, MultipartFile file) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new InvalidUserIdException("유효한 사용자 ID가 아닙니다."));

        String oldImageUrl = member.getImage();
        String imageUrl = oldImageUrl;
        if (file != null && !file.isEmpty()) {
            imageUrl = uploadFileToS3(file);
            if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                deleteFileFromS3(oldImageUrl);
            }
        }

        member.updateProfile(updateProfileDTO.getUserName(), updateProfileDTO.getEmail(), imageUrl);

        memberRepository.save(member);
    }

    private String uploadFileToS3(MultipartFile file) {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());  // Content-Type 설정
        metadata.setContentDisposition("inline");  // Content-Disposition 설정

        try {
            amazonS3.putObject(bucketName, fileName, file.getInputStream(), metadata);
            amazonS3.setObjectAcl(bucketName, fileName, CannedAccessControlList.PublicRead);  // Set ACL to public-read
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }

        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    private void deleteFileFromS3(String fileUrl) {
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        amazonS3.deleteObject(bucketName, fileName);
    }

    @Transactional(readOnly = true)
    public MyPageContractListDTO getMyPageContracts(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new InvalidUserIdException("유효한 사용자 ID가 아닙니다."));

        List<Contract> lendContracts = contractRepository.findByCategoryAndLendMember(Category.BORROW, member);
        List<Contract> borrowContracts = contractRepository.findByCategoryAndBorrowMember(Category.LEND, member);
        List<Contract> contracts = new ArrayList<>();
        contracts.addAll(lendContracts);
        contracts.addAll(borrowContracts);

        List<MyPageContractListDTO.LendingItem> lendingItems = contracts.stream()
                .filter(contract -> contract.getCategory() == Category.LEND)
                .sorted(Comparator.comparing(contract -> contract.getItem().getCreatedAt(), Comparator.reverseOrder()))
                .map(contract -> {
                    List<String> itemHashList;
                    try {
                        itemHashList = objectMapper.readValue(contract.getItem().getItemHash(), List.class);
                    } catch (IOException e) {
                        throw new RuntimeException("아이템 해시를 리스트로 변환하는데 실패했습니다.", e);
                    }
                    return MyPageContractListDTO.LendingItem.builder()
                            .contractId(contract.getContractId())
                            .itemName(contract.getItem().getItemName())
                            .price(contract.getItem().getPrice())
                            .itemPlace(contract.getItem().getItemPlace())
                            .time(contract.getItem().getTime())
                            .contractTime(contract.getItem().getContractTime())
                            .itemHash(itemHashList)
                            .likeStatus(loveRepository.existsByItemAndMember(contract.getItem(), member))
                            .donateStatus(contract.getItem().getPrice() == 0)
                            .build();
                })
                .collect(Collectors.toList());

        List<MyPageContractListDTO.BorrowingItem> borrowingItems = contracts.stream()
                .filter(contract -> contract.getCategory() == Category.BORROW)
                .sorted(Comparator.comparing(contract -> contract.getItem().getCreatedAt(), Comparator.reverseOrder()))
                .map(contract -> {
                    List<String> itemHashList;
                    try {
                        itemHashList = objectMapper.readValue(contract.getItem().getItemHash(), List.class);
                    } catch (IOException e) {
                        throw new RuntimeException("아이템 해시를 리스트로 변환하는데 실패했습니다.", e);
                    }
                    return MyPageContractListDTO.BorrowingItem.builder()
                            .contractId(contract.getContractId())
                            .itemName(contract.getItem().getItemName())
                            .itemImage(contract.getItem().getItemImage())
                            .price(contract.getItem().getPrice())
                            .itemPlace(contract.getItem().getItemPlace())
                            .time(contract.getItem().getTime())
                            .contractTime(contract.getItem().getContractTime())
                            .itemHash(itemHashList)
                            .likeStatus(loveRepository.existsByItemAndMember(contract.getItem(), member))
                            .donateStatus(contract.getItem().getPrice() == 0)
                            .build();
                })
                .collect(Collectors.toList());

        return MyPageContractListDTO.builder()
                .lendingItems(lendingItems)
                .borrowingItems(borrowingItems)
                .build();
    }

    @Transactional(readOnly = true)
    public MyPageContractListDTO getCompleteContracts(Long memberId) {
        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new InvalidUserIdException("유효한 사용자 ID가 아닙니다."));

        List<Alarm> completedAlarms = alarmRepository.findByLendMemberOrBorrowMember(member, member).stream()
                .filter(alarm -> alarm.getAlarmStatus() == AlarmStatus.COMPLETE)
                .collect(Collectors.toList());

        List<MyPageContractListDTO.LendingItem> lendingItems = completedAlarms.stream()
                .filter(alarm -> alarm.getContract().getCategory() == Category.LEND)
                .sorted(Comparator.comparing(alarm -> alarm.getContract().getItem().getCreatedAt(), Comparator.reverseOrder()))
                .map(alarm -> {
                    List<String> itemHashList;
                    try {
                        itemHashList = objectMapper.readValue(alarm.getContract().getItem().getItemHash(), List.class);
                    } catch (IOException e) {
                        throw new RuntimeException("아이템 해시를 리스트로 변환하는데 실패했습니다.", e);
                    }
                    return MyPageContractListDTO.LendingItem.builder()
                            .contractId(alarm.getContract().getContractId())
                            .itemName(alarm.getContract().getItem().getItemName())
                            .price(alarm.getContract().getItem().getPrice())
                            .itemPlace(alarm.getContract().getItem().getItemPlace())
                            .time(alarm.getContract().getItem().getTime())
                            .contractTime(alarm.getContract().getItem().getContractTime())
                            .itemHash(itemHashList)
                            .likeStatus(loveRepository.existsByItemAndMember(alarm.getContract().getItem(), member))
                            .donateStatus(alarm.getContract().getItem().getPrice() == 0)
                            .build();
                })
                .collect(Collectors.toList());

        List<MyPageContractListDTO.BorrowingItem> borrowingItems = completedAlarms.stream()
                .filter(alarm -> alarm.getContract().getCategory() == Category.BORROW)
                .sorted(Comparator.comparing(alarm -> alarm.getContract().getItem().getCreatedAt(), Comparator.reverseOrder()))
                .map(alarm -> {
                    List<String> itemHashList;
                    try {
                        itemHashList = objectMapper.readValue(alarm.getContract().getItem().getItemHash(), List.class);
                    } catch (IOException e) {
                        throw new RuntimeException("아이템 해시를 리스트로 변환하는데 실패했습니다.", e);
                    }
                    return MyPageContractListDTO.BorrowingItem.builder()
                            .contractId(alarm.getContract().getContractId())
                            .itemName(alarm.getContract().getItem().getItemName())
                            .itemImage(alarm.getContract().getItem().getItemImage())
                            .price(alarm.getContract().getItem().getPrice())
                            .itemPlace(alarm.getContract().getItem().getItemPlace())
                            .time(alarm.getContract().getItem().getTime())
                            .contractTime(alarm.getContract().getItem().getContractTime())
                            .itemHash(itemHashList)
                            .likeStatus(loveRepository.existsByItemAndMember(alarm.getContract().getItem(), member))
                            .donateStatus(alarm.getContract().getItem().getPrice() == 0)
                            .build();
                })
                .collect(Collectors.toList());

        return MyPageContractListDTO.builder()
                .lendingItems(lendingItems)
                .borrowingItems(borrowingItems)
                .build();
    }
}