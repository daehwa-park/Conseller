package com.conseller.conseller.inquiry;

import com.conseller.conseller.entity.Inquiry;
import com.conseller.conseller.entity.User;
import com.conseller.conseller.inquiry.dto.mapper.InquiryMapper;
import com.conseller.conseller.inquiry.dto.request.AnswerInquiryRequest;
import com.conseller.conseller.inquiry.dto.request.RegistInquiryRequest;
import com.conseller.conseller.inquiry.dto.response.DetailInquiryResponse;
import com.conseller.conseller.inquiry.dto.response.InquiryItemData;
import com.conseller.conseller.inquiry.dto.response.InquiryListResponse;
import com.conseller.conseller.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InquiryServiceImpl implements InquiryService{
    private final UserRepository userRepository;
    private final InquiryRepository inquiryRepository;

    @Override
    public void registInquiry(RegistInquiryRequest request) {
        User user = userRepository.findById(request.getUserIdx())
                .orElseThrow(() -> new RuntimeException());

        Inquiry inquiry = InquiryMapper.INSTANCE.registInquiryToInquiry(request, user);

        inquiryRepository.save(inquiry);
    }

    @Override
    public InquiryListResponse getInquiryList() {
        List<Inquiry> inquiryList = inquiryRepository.findAll(Sort.by(Sort.Order.desc("inquiryCreatedDate")));

        List<InquiryItemData> itemData = InquiryMapper.INSTANCE.inquirysToItemDatas(inquiryList);

        InquiryListResponse response = new InquiryListResponse(itemData);

        return response;
    }

    @Override
    public DetailInquiryResponse detailInquiry(Long inquiryIdx) {
        Inquiry inquiry = inquiryRepository.findById(inquiryIdx)
                .orElseThrow(() -> new RuntimeException());

        User user = userRepository.findById(inquiry.getUser().getUserIdx())
                .orElseThrow(() -> new RuntimeException());

        DetailInquiryResponse response = InquiryMapper.INSTANCE.entityToDetailInquiryResponse(user, inquiry);

        return response;
    }

    @Override
    public void answerInquiry(Long inquiryIdx, AnswerInquiryRequest request) {
        Inquiry inquiry = inquiryRepository.findById(inquiryIdx)
                .orElseThrow(() -> new RuntimeException());

        inquiry.setInquiryAnswer(request.getInquiryAnswer());
        inquiry.setInquiryAnswerDate(LocalDateTime.now());

    }
}