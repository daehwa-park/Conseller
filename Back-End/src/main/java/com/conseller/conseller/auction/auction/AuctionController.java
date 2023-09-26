package com.conseller.conseller.auction.auction;

import com.conseller.conseller.auction.auction.dto.request.AuctionListRequest;
import com.conseller.conseller.auction.auction.dto.request.ModifyAuctionRequest;
import com.conseller.conseller.auction.auction.dto.request.RegistAuctionRequest;
import com.conseller.conseller.auction.auction.dto.response.AuctionListResponse;
import com.conseller.conseller.auction.auction.dto.response.AuctionTradeResponse;
import com.conseller.conseller.auction.auction.dto.response.DetailAuctionResponse;
import com.conseller.conseller.auction.auction.dto.response.RegistAuctionResponse;
import com.conseller.conseller.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auction")
public class AuctionController {
    private final AuctionService auctionService;
    private final NotificationService notificationService;

    // 경매 목록
    @PostMapping
    public ResponseEntity<AuctionListResponse> getAuctionList(@RequestBody AuctionListRequest request) {
        AuctionListResponse response = auctionService.getAuctionList(request);

        return ResponseEntity.ok()
                .body(response);
    }

    // 경매 글 등록
    @PostMapping("/regist")
    public ResponseEntity<RegistAuctionResponse> registAuction(@RequestBody RegistAuctionRequest request) {
        Long auctionIdx = auctionService.registAuction(request);

        RegistAuctionResponse response = new RegistAuctionResponse(auctionIdx);

        return ResponseEntity.ok()
                .body(response);
    }

    // 경매 글 상세 보기
    @GetMapping("/{auction_idx}")
    public ResponseEntity<DetailAuctionResponse> detailAuction(@PathVariable("auction_idx") Long auctionIdx) {
        DetailAuctionResponse response =  auctionService.detailAuction(auctionIdx);

        return ResponseEntity.ok()
                .body(response);
    }

    // 경매 글 수정
    @PatchMapping("/{auction_idx}")
    public ResponseEntity<Object> modifyAuction(@PathVariable("auction_idx") Long auctionIdx, @RequestBody ModifyAuctionRequest auctionRequest) {
        auctionService.modifyAuction(auctionIdx, auctionRequest);

        return ResponseEntity.ok()
                .build();
    }

    // 경매 글 삭제
    @DeleteMapping("/{auction_idx}")
    public ResponseEntity<Object> deleteAuction(@PathVariable("auction_idx") Long auctionIdx) {
        auctionService.deleteAuction(auctionIdx);

        return ResponseEntity.ok()
                .build();
    }

    // 경매 거래 진행
    @GetMapping("/trade/{auction_idx}")
    public ResponseEntity<AuctionTradeResponse> tradeAuction(@PathVariable("auction_idx") Long auctionIdx, @RequestParam(name = "im") Integer index ) {
        AuctionTradeResponse response = auctionService.tradeAuction(auctionIdx, index);

        notificationService.sendAuctionNotification(auctionIdx, "경매 거래 진행", "님과의 거래가 시작되었습니다.", index, 1);

        return ResponseEntity.ok()
                .body(response);
    }

    // 경매 진행 취소
    @PatchMapping("/cancel/{auction_idx}")
    public ResponseEntity<Object> cancelAuction(@PathVariable("auction_idx") Long auctionIdx) {
        auctionService.cancelAuction(auctionIdx);

        // 거래 취소 알림
        notificationService.sendAuctionNotification(auctionIdx, "경매 거래 취소", "님과의 거래가 취소되었습니다", 1, 1);
        notificationService.sendAuctionNotification(auctionIdx, "경매 거래 취소", "님과의 거래가 취소되었습니다", 2, 1);

        return ResponseEntity.ok()
                .build();
    }

    // 입금 완료 버튼
    @PatchMapping("/complete/{auction_idx}")
    public ResponseEntity<Object> completeAuction(@PathVariable("auction_idx") Long auctionIdx) {
        // 판매자에게 알림
        notificationService.sendAuctionNotification(auctionIdx, "경매 입금 완료", "님이 입금을 완료하였습니다.", 2, 1);

        return ResponseEntity.ok()
                .build();
    }

    // 거래 확정 버튼
    @PatchMapping("/confirm/{auction_idx}")
    public  ResponseEntity<Object> confirmAuction(@PathVariable("auction_idx") Long auctionIdx) {
        auctionService.confirmAuction(auctionIdx);

        notificationService.sendAuctionNotification(auctionIdx, "경매 거래 완료", "님과의 거래가 완료되었습니다.", 1, 1);

        return ResponseEntity.ok()
                .build();
    }


}
