package com.example.AIkiosk.controller;


import com.example.AIkiosk.dto.CoffeeMenuDTO;
import com.example.AIkiosk.service.CoffeeMenuService;
import com.example.AIkiosk.service.CoffeeOrderService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@Controller
public class CoffeeMenuController {

    @Autowired
    private CoffeeMenuService coffeeMenuService;

    @Autowired
    private CoffeeOrderService coffeeOrderService;

    @GetMapping("/")
    public String showCoffeeMenu(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6") int itemsPerPage,
            @RequestParam(required = false) Integer new_face_id,
            Model model, HttpServletResponse response) {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html");

        // ✅ 페이징된 메뉴 가져오기
        List<CoffeeMenuDTO> coffeeMenuList = coffeeMenuService.getPagedCoffeeMenu(page, itemsPerPage);
        int totalItems = coffeeMenuService.getTotalItems();
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);

        // ✅ 페이지 목록 생성
        List<PageInfo> pages = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pages.add(new PageInfo(i, i == page));
        }

        model.addAttribute("coffeeMenuList", coffeeMenuList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("new_face_id", new_face_id);
        model.addAttribute("pages", pages);

        return "index";
    }

    // ✅ 페이지 정보를 담는 내부 클래스
    public static class PageInfo {
        private final int pageNumber;
        private final boolean isCurrent;

        public PageInfo(int pageNumber, boolean isCurrent) {
            this.pageNumber = pageNumber;
            this.isCurrent = isCurrent;
        }

        public int getPageNumber() { return pageNumber; }
        public boolean getIsCurrent() { return isCurrent; }
    }

    /*
    @GetMapping("/recommend")
    public String recommendCoffee(
            @RequestParam(value = "coffeeName", required = false, defaultValue = "아메리카노") String coffeeName,
            @RequestParam(value = "coffeeImage", required = false, defaultValue = "/img/iceAmericano.jpg") String coffeeImage,
            @RequestParam(value = "totalOrderCount", required = false, defaultValue = "0") int totalOrderCount,
            @RequestParam(value = "coffeeOrderCount", required = false, defaultValue = "0") int coffeeOrderCount,
            @RequestParam(value = "lastOrderDate", required = false) String lastOrderDate,
            @RequestParam(value = "lastOrderFaceImage", required = false) String lastOrderFaceImage,
            @RequestParam(value = "coffee_id", required = false) int coffeeId,
            @RequestParam(value = "new_face_id", required = false) int newFaceId,
            @RequestParam(value = "top2CoffeeId", required = false) int top2CoffeeId,
            @RequestParam(value = "top3CoffeeId", required = false) int top3CoffeeId,
            @RequestParam(value = "top2CoffeeName", required = false) String top2CoffeeName,
            @RequestParam(value = "top3CoffeeName", required = false) String top3CoffeeName,
            @RequestParam(value = "totalCoffeeCount", required = false) int totalCoffeeCount,
            Model model) {

        //변동성 있어서 현재 사용안함
        int visitCount = totalOrderCount + 1; // 현재 방문자 수 계산

        model.addAttribute("coffeeName", coffeeName);
        model.addAttribute("coffeeImage", coffeeImage);
        model.addAttribute("totalOrderCount", totalOrderCount);
        model.addAttribute("coffeeOrderCount", coffeeOrderCount);

        model.addAttribute("lastOrderDate", lastOrderDate);
        model.addAttribute("lastOrderFaceImage", lastOrderFaceImage);

        model.addAttribute("top2CoffeeId", top2CoffeeId);
        model.addAttribute("top3CoffeeId", top3CoffeeId);
        model.addAttribute("top2CoffeeName", top2CoffeeName);
        model.addAttribute("top3CoffeeName", top3CoffeeName);


        model.addAttribute("totalCoffeeCount", totalCoffeeCount);

        model.addAttribute("coffeeId", coffeeId);
        model.addAttribute("newFaceId", newFaceId);


        return "recommendation";
    }
    */
    @GetMapping("/recommend")
    public String recommendCoffee(
            @RequestParam(value = "coffeeName", required = false) String coffeeName,
            @RequestParam(value = "coffeeImage", required = false) String coffeeImage,
            @RequestParam(value = "totalOrderCount", required = false) Integer totalOrderCount,
            @RequestParam(value = "coffeeOrderCount", required = false) Integer coffeeOrderCount,
            @RequestParam(value = "lastOrderDate", required = false) String lastOrderDate,
            @RequestParam(value = "lastOrderFaceImage", required = false) String lastOrderFaceImage,
            @RequestParam(value = "coffee_id", required = false) Integer coffeeId,
            @RequestParam(value = "new_face_id", required = false) Integer newFaceId,
            @RequestParam(value = "top2CoffeeId", required = false) String top2CoffeeIdStr,
            @RequestParam(value = "top3CoffeeId", required = false) String top3CoffeeIdStr,
            @RequestParam(value = "top2CoffeeName", required = false) String top2CoffeeName,
            @RequestParam(value = "top3CoffeeName", required = false) String top3CoffeeName,
            @RequestParam(value = "totalCoffeeCount", required = false) Integer totalCoffeeCount,
            Model model) {

        // top2CoffeeId, top3CoffeeId String에서 바로 int 변환 (null 또는 "null"은 0으로)
        Integer top2CoffeeId = 0;
        if (top2CoffeeIdStr != null && !top2CoffeeIdStr.equalsIgnoreCase("null") && !top2CoffeeIdStr.isEmpty()) {
            try {
                top2CoffeeId = Integer.parseInt(top2CoffeeIdStr);
            } catch (NumberFormatException e) {
                top2CoffeeId = 0;
            }
        }

        Integer top3CoffeeId = 0;
        if (top3CoffeeIdStr != null && !top3CoffeeIdStr.equalsIgnoreCase("null") && !top3CoffeeIdStr.isEmpty()) {
            try {
                top3CoffeeId = Integer.parseInt(top3CoffeeIdStr);
            } catch (NumberFormatException e) {
                top3CoffeeId = 0;
            }
        }

        boolean disableTop2 = (top2CoffeeId == 0);
        boolean disableTop3 = (top3CoffeeId == 0);

        model.addAttribute("disableTop2", disableTop2);
        model.addAttribute("disableTop3", disableTop3);



        coffeeName = (coffeeName != null) ? coffeeName : "데이터 없음";
        coffeeImage = (coffeeImage != null) ? coffeeImage : "/img/iceAmericano.jpg";
        totalOrderCount = (totalOrderCount != null) ? totalOrderCount : 0;
        coffeeOrderCount = (coffeeOrderCount != null) ? coffeeOrderCount : 0;
        lastOrderDate = (lastOrderDate != null) ? lastOrderDate : "데이터 없음";
        lastOrderFaceImage = (lastOrderFaceImage != null) ? lastOrderFaceImage : "/img/unknown_face.jpg";
        coffeeId = (coffeeId != null) ? coffeeId : 0;
        newFaceId = (newFaceId != null) ? newFaceId : 0;
        top2CoffeeName = (top2CoffeeName != null) ? top2CoffeeName : "데이터 없음";
        top3CoffeeName = (top3CoffeeName != null) ? top3CoffeeName : "데이터 없음";
        totalCoffeeCount = (totalCoffeeCount != null) ? totalCoffeeCount : 0;

        model.addAttribute("coffeeName", coffeeName);
        model.addAttribute("coffeeImage", coffeeImage);
        model.addAttribute("totalOrderCount", totalOrderCount);
        model.addAttribute("coffeeOrderCount", coffeeOrderCount);
        model.addAttribute("lastOrderDate", lastOrderDate);
        model.addAttribute("lastOrderFaceImage", lastOrderFaceImage);
        model.addAttribute("coffeeId", coffeeId);
        model.addAttribute("newFaceId", newFaceId);
        model.addAttribute("top2CoffeeId", top2CoffeeId);
        model.addAttribute("top3CoffeeId", top3CoffeeId);
        model.addAttribute("top2CoffeeName", top2CoffeeName);
        model.addAttribute("top3CoffeeName", top3CoffeeName);
        model.addAttribute("totalCoffeeCount", totalCoffeeCount);






        return "recommendation";
    }



    @GetMapping("/latest")
    public String recommendCoffee(
            @RequestParam(value = "new_face_id", required = false) String newFaceIdStr,

            @RequestParam(value = "face_id_1", required = false) String faceId1Str,
            @RequestParam(value = "image_path_1", required = false) String imagePath1,
            @RequestParam(value = "coffee_id_1", required = false) String coffeeId1Str,
            @RequestParam(value = "coffee_name_1", required = false) String coffeeName1,
            @RequestParam(value = "coffee_image_url_1", required = false) String coffeeImageUrl1,
            @RequestParam(value = "latest_order_1", required = false) String latestOrder1,

            @RequestParam(value = "face_id_2", required = false) String faceId2Str,
            @RequestParam(value = "image_path_2", required = false) String imagePath2,
            @RequestParam(value = "coffee_id_2", required = false) String coffeeId2Str,
            @RequestParam(value = "coffee_name_2", required = false) String coffeeName2,
            @RequestParam(value = "coffee_image_url_2", required = false) String coffeeImageUrl2,
            @RequestParam(value = "latest_order_2", required = false) String latestOrder2,

            @RequestParam(value = "face_id_3", required = false) String faceId3Str,
            @RequestParam(value = "image_path_3", required = false) String imagePath3,
            @RequestParam(value = "coffee_id_3", required = false) String coffeeId3Str,
            @RequestParam(value = "coffee_name_3", required = false) String coffeeName3,
            @RequestParam(value = "coffee_image_url_3", required = false) String coffeeImageUrl3,
            @RequestParam(value = "latest_order_3", required = false) String latestOrder3,
            Model model) {

        Long newFaceId = 0L;
        if (newFaceIdStr != null && !newFaceIdStr.equalsIgnoreCase("null") && !newFaceIdStr.isEmpty()) {
            try {
                newFaceId = Long.parseLong(newFaceIdStr);
            } catch (NumberFormatException e) {
                newFaceId = 0L;
            }
        }

        Long faceId1 = 0L;
        if (faceId1Str != null && !faceId1Str.equalsIgnoreCase("null") && !faceId1Str.isEmpty()) {
            try {
                faceId1 = Long.parseLong(faceId1Str);
            } catch (NumberFormatException e) {
                faceId1 = 0L;
            }
        }
        imagePath1 = (imagePath1 != null) ? imagePath1 : "/img/unknown_face.jpg";

        Long coffeeId1 = 0L;
        if (coffeeId1Str != null && !coffeeId1Str.equalsIgnoreCase("null") && !coffeeId1Str.isEmpty()) {
            try {
                coffeeId1 = Long.parseLong(coffeeId1Str);
            } catch (NumberFormatException e) {
                coffeeId1 = 0L;
            }
        }
        coffeeName1 = (coffeeName1 != null) ? coffeeName1 : "데이터 없음";
        coffeeImageUrl1 = (coffeeImageUrl1 != null) ? coffeeImageUrl1 : "/img/no_image.jpg";
        latestOrder1 = (latestOrder1 != null) ? latestOrder1 : "";

        Long faceId2 = 0L;
        if (faceId2Str != null && !faceId2Str.equalsIgnoreCase("null") && !faceId2Str.isEmpty()) {
            try {
                faceId2 = Long.parseLong(faceId2Str);
            } catch (NumberFormatException e) {
                faceId2 = 0L;
            }
        }
        imagePath2 = (imagePath2 != null) ? imagePath2 : "/img/unknown_face.jpg";

        Long coffeeId2 = 0L;
        if (coffeeId2Str != null && !coffeeId2Str.equalsIgnoreCase("null") && !coffeeId2Str.isEmpty()) {
            try {
                coffeeId2 = Long.parseLong(coffeeId2Str);
            } catch (NumberFormatException e) {
                coffeeId2 = 0L;
            }
        }
        coffeeName2 = (coffeeName2 != null) ? coffeeName2 : "데이터 없음";
        coffeeImageUrl2 = (coffeeImageUrl2 != null) ? coffeeImageUrl2 : "/img/no_image.jpg";
        latestOrder2 = (latestOrder2 != null) ? latestOrder2 : "";

        Long faceId3 = 0L;
        if (faceId3Str != null && !faceId3Str.equalsIgnoreCase("null") && !faceId3Str.isEmpty()) {
            try {
                faceId3 = Long.parseLong(faceId3Str);
            } catch (NumberFormatException e) {
                faceId3 = 0L;
            }
        }
        imagePath3 = (imagePath3 != null) ? imagePath3 : "/img/unknown_face.jpg";

        Long coffeeId3 = 0L;
        if (coffeeId3Str != null && !coffeeId3Str.equalsIgnoreCase("null") && !coffeeId3Str.isEmpty()) {
            try {
                coffeeId3 = Long.parseLong(coffeeId3Str);
            } catch (NumberFormatException e) {
                coffeeId3 = 0L;
            }
        }

        model.addAttribute("disableCoffee1", (coffeeId1 == 0));
        model.addAttribute("disableCoffee2", (coffeeId2 == 0));
        model.addAttribute("disableCoffee3", (coffeeId3 == 0));

        coffeeName3 = (coffeeName3 != null) ? coffeeName3 : "데이터 없음";
        coffeeImageUrl3 = (coffeeImageUrl3 != null) ? coffeeImageUrl3 : "/img/no_image.jpg";
        latestOrder3 = (latestOrder3 != null) ? latestOrder3 : "";

        String inputFormat = "EEE, dd MMM yyyy HH:mm:ss z";
        String outputFormat = "yyyy년 MM월 dd일 HH시 mm분";

        String formattedOrder1 = !latestOrder1.isEmpty() ? formatDate(latestOrder1, inputFormat, outputFormat) : "날짜 없음";
        String formattedOrder2 = !latestOrder2.isEmpty() ? formatDate(latestOrder2, inputFormat, outputFormat) : "날짜 없음";
        String formattedOrder3 = !latestOrder3.isEmpty() ? formatDate(latestOrder3, inputFormat, outputFormat) : "날짜 없음";

        model.addAttribute("newFaceId", newFaceId);

        model.addAttribute("face1", faceId1);
        model.addAttribute("image1", imagePath1);
        model.addAttribute("coffeeId1", coffeeId1);
        model.addAttribute("coffeeName1", coffeeName1);
        model.addAttribute("coffeeImage1", coffeeImageUrl1);
        model.addAttribute("orderTime1", formattedOrder1);

        model.addAttribute("face2", faceId2);
        model.addAttribute("image2", imagePath2);
        model.addAttribute("coffeeId2", coffeeId2);
        model.addAttribute("coffeeName2", coffeeName2);
        model.addAttribute("coffeeImage2", coffeeImageUrl2);
        model.addAttribute("orderTime2", formattedOrder2);

        model.addAttribute("face3", faceId3);
        model.addAttribute("image3", imagePath3);
        model.addAttribute("coffeeId3", coffeeId3);
        model.addAttribute("coffeeName3", coffeeName3);
        model.addAttribute("coffeeImage3", coffeeImageUrl3);
        model.addAttribute("orderTime3", formattedOrder3);

        return "latestRecommend";
    }


    public static String formatDate(String inputDate, String inputFormat, String outputFormat) {
        try {
            SimpleDateFormat inputSdf = new SimpleDateFormat(inputFormat, Locale.ENGLISH);
            inputSdf.setTimeZone(TimeZone.getTimeZone("GMT"));

            Date date = inputSdf.parse(inputDate);

            SimpleDateFormat outputSdf = new SimpleDateFormat(outputFormat);
            outputSdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

            return outputSdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return inputDate;
        }
    }



    @GetMapping("/cancel")
    public String cancel() {
        // 취소하면 메인 페이지로 이동
        return "redirect:/";
    }

    @RequestMapping(value = "/pay", method = RequestMethod.POST)
    public ResponseEntity<?> saveOrders(@RequestBody Map<String, Object> requestData) {
        // requestData에서 faceId와 cartItems를 추출
        Long faceId = Long.parseLong(requestData.get("faceId").toString());
        Map<String, Object> cartItems = (Map<String, Object>) requestData.get("cartItems");

        // cartItems 처리 로직
        for (Map.Entry<String, Object> entry : cartItems.entrySet()) {
            // 각 커피에 대한 아이템 데이터 처리
            Map<String, Object> item = (Map<String, Object>) entry.getValue();

            Long coffeeId = Long.parseLong(item.get("id").toString());
            int quantity = Integer.parseInt(item.get("quantity").toString());

            // CoffeeOrderService로 주문 저장
            coffeeOrderService.saveOrderMultipleTimes(faceId, coffeeId, quantity);
        }

        return ResponseEntity.ok("주문 저장 완료");
    }

    @PostMapping("/pay_single")
    public ResponseEntity<String> paySingle(
            @RequestParam("faceId") Long faceId,
            @RequestParam("coffeeId") Long coffeeId) {

        System.out.println(faceId);
        System.out.println(coffeeId);

        coffeeOrderService.saveOrder(faceId, coffeeId);
        return ResponseEntity.ok("결제 및 저장 완료!");
    }

    @GetMapping("/load")
    public String showLoadPage() {
        return "load"; // load.mustache
    }




}
