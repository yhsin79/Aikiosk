package com.example.AIkiosk.controller;


import com.example.AIkiosk.dto.CoffeeMenuDTO;
import com.example.AIkiosk.dto.FaceWithCoffeeDTO;
import com.example.AIkiosk.repository.CoffeeOrderRepository;
import com.example.AIkiosk.service.CoffeeMenuService;
import com.example.AIkiosk.service.CoffeeOrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.io.StringReader;


import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.net.URLEncoder;


@Controller
public class CoffeeMenuController {

    @Autowired
    private CoffeeMenuService coffeeMenuService;

    @Autowired
    private CoffeeOrderService coffeeOrderService;

    @Autowired
    private CoffeeOrderRepository coffeeOrderRepository;

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


    // 백업용
    /*
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
            @RequestParam(value = "temp_type", required = false) String temp_type,

            @RequestParam(value = "top2CoffeeId", required = false) String top2CoffeeIdStr,
            @RequestParam(value = "top3CoffeeId", required = false) String top3CoffeeIdStr,
            @RequestParam(value = "top2CoffeeName", required = false) String top2CoffeeName,
            @RequestParam(value = "top3CoffeeName", required = false) String top3CoffeeName,
            @RequestParam(value = "top2_temp_type", required = false) String top2_temp_type,
            @RequestParam(value = "top3_temp_type", required = false) String top3_temp_type,

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

        temp_type = (temp_type != null) ? temp_type : "데이터 없음";
        top2_temp_type = (top2_temp_type != null) ? top2_temp_type : "데이터 없음";
        top3_temp_type = (top3_temp_type != null) ? top3_temp_type : "데이터 없음";

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
        model.addAttribute("temp_type",temp_type);
        model.addAttribute("top2_temp_type",top2_temp_type);
        model.addAttribute("top3_temp_type",top3_temp_type);
        model.addAttribute("top2CoffeeName", top2CoffeeName);
        model.addAttribute("top3CoffeeName", top3CoffeeName);
        model.addAttribute("totalCoffeeCount", totalCoffeeCount);

        boolean weatherApiOk = false;

        try {
            String serviceKey = "343b26b1abb17b48e2c8a95d700cbed28dcd9d5d33e0fffa9e0951c4419bc273";
            String nx = "62";
            String ny = "120";

            LocalDateTime now = LocalDateTime.now();
            String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            int hour = now.getHour();
            String baseTime;
            if (hour < 2) baseTime = "2300";
            else if (hour < 5) baseTime = "0200";
            else if (hour < 8) baseTime = "0500";
            else if (hour < 11) baseTime = "0800";
            else if (hour < 14) baseTime = "1100";
            else if (hour < 17) baseTime = "1400";
            else if (hour < 20) baseTime = "1700";
            else if (hour < 23) baseTime = "2000";
            else baseTime = "2300";

            StringBuilder urlBuilder = new StringBuilder(
                    "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst"
            );
            urlBuilder.append("?serviceKey=").append(URLEncoder.encode(serviceKey, "UTF-8"));
            urlBuilder.append("&pageNo=1");
            urlBuilder.append("&numOfRows=100");
            urlBuilder.append("&dataType=JSON");
            urlBuilder.append("&base_date=").append(baseDate);
            urlBuilder.append("&base_time=").append(baseTime);
            urlBuilder.append("&nx=").append(nx);
            urlBuilder.append("&ny=").append(ny);

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(responseCode >= 200 && responseCode <= 300 ?
                            conn.getInputStream() : conn.getErrorStream())
            );

            StringBuilder result = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            conn.disconnect();

            if (responseCode == 200) {
                weatherApiOk = true;
            }

            // HTTP 응답 확인
            if (responseCode != 200) {
                System.out.println("API 호출 실패: " + responseCode);
                System.out.println("응답 내용: " + result.toString());
                // 기본값 세팅
                model.addAttribute("tmpValue", "데이터 없음");
                model.addAttribute("ptyText", "데이터 없음");
                model.addAttribute("popValue", "데이터 없음");
                model.addAttribute("skyText", "데이터 없음");
                model.addAttribute("rehValue", "데이터 없음");
                model.addAttribute("tmx", "데이터 없음");
                model.addAttribute("tmn", "데이터 없음");
                model.addAttribute("weatherApiOk", weatherApiOk);
                return "recommendation";
            }

            // JSON 파싱
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.readValue(result.toString(), Map.class);

            List<Map<String, Object>> items = (List<Map<String, Object>>)
                    ((Map)((Map)((Map)map.get("response")).get("body")).get("items")).get("item");

            int nowHour = now.getHour();
            String closestTime = "";
            int minDiff = 9999;
            String tmpValue = "", ptyValue = "", popValue = "", skyValue = "", rehValue = "";
            String tmx = "", tmn = "";

            for (Map<String, Object> item : items) {
                String category = (String) item.get("category");
                String fcstTime = (String) item.get("fcstTime");
                int fcstHour = Integer.parseInt(fcstTime.substring(0, 2));

                if ("TMP".equals(category)) {
                    int diff = Math.abs(fcstHour - nowHour);
                    if (diff < minDiff) {
                        minDiff = diff;
                        tmpValue = (String) item.get("fcstValue");
                        closestTime = fcstTime;
                    }
                } else if ("PTY".equals(category)) {
                    ptyValue = (String) item.get("fcstValue");
                } else if ("POP".equals(category)) {
                    popValue = (String) item.get("fcstValue");
                } else if ("SKY".equals(category)) {
                    skyValue = (String) item.get("fcstValue");
                } else if ("REH".equals(category)) {
                    rehValue = (String) item.get("fcstValue");
                } else if ("TMX".equals(category) && item.get("fcstDate").equals(baseDate)) {
                    tmx = (String) item.get("fcstValue");
                } else if ("TMN".equals(category) && item.get("fcstDate").equals(baseDate)) {
                    tmn = (String) item.get("fcstValue");
                }
            }

            String skyText = switch (skyValue) {
                case "1" -> "맑음";
                case "3" -> "구름많음";
                case "4" -> "흐림";
                default -> "알수없음";
            };
            String ptyText = switch (ptyValue) {
                case "0" -> "없음";
                case "1" -> "비";
                case "2" -> "비/눈";
                case "3" -> "눈";
                case "4" -> "소나기";
                default -> "알수없음";
            };

            System.out.println("현재 시간 기준 TMP(" + closestTime + "): " + tmpValue);
            System.out.println("강수 형태(PTY): " + ptyText);
            System.out.println("강수 확률(POP): " + popValue + "%");
            System.out.println("하늘 상태(SKY): " + skyText);
            System.out.println("습도(REH): " + rehValue + "%");
            System.out.println("일 최고 기온(TMX): " + tmx);
            System.out.println("일 최저 기온(TMN): " + tmn);

            // 모델에 넣기
            model.addAttribute("tmpValue", tmpValue);
            model.addAttribute("ptyText", ptyText);
            model.addAttribute("popValue", popValue);
            model.addAttribute("skyText", skyText);
            model.addAttribute("rehValue", rehValue);
            model.addAttribute("tmx", tmx);
            model.addAttribute("tmn", tmn);
            model.addAttribute("weatherApiOk", weatherApiOk);

        } catch (Exception e) {
            e.printStackTrace();
            // 예외 발생 시 기본값 세팅
            model.addAttribute("tmpValue", "데이터 없음");
            model.addAttribute("ptyText", "데이터 없음");
            model.addAttribute("popValue", "데이터 없음");
            model.addAttribute("skyText", "데이터 없음");
            model.addAttribute("rehValue", "데이터 없음");
            model.addAttribute("tmx", "데이터 없음");
            model.addAttribute("tmn", "데이터 없음");
            model.addAttribute("weatherApiOk", weatherApiOk);
        }




        return "recommendation";
    }
    */

    @PostMapping("/recommend")
    public String recommendCoffee(
            @RequestParam(value = "coffeeName", required = false) String coffeeName,
            @RequestParam(value = "coffeeImage", required = false) String coffeeImage,
            @RequestParam(value = "totalOrderCount", required = false) Integer totalOrderCount,
            @RequestParam(value = "coffeeOrderCount", required = false) Integer coffeeOrderCount,
            @RequestParam(value = "lastOrderDate", required = false) String lastOrderDate,
            @RequestParam(value = "lastOrderFaceImage", required = false) String lastOrderFaceImage,
            @RequestParam(value = "coffee_id", required = false) Integer coffeeId,
            @RequestParam(value = "new_face_id", required = false) Integer newFaceId,
            @RequestParam(value = "temp_type", required = false) String temp_type,

            @RequestParam(value = "top2CoffeeId", required = false) String top2CoffeeIdStr,
            @RequestParam(value = "top3CoffeeId", required = false) String top3CoffeeIdStr,
            @RequestParam(value = "top2CoffeeName", required = false) String top2CoffeeName,
            @RequestParam(value = "top3CoffeeName", required = false) String top3CoffeeName,
            @RequestParam(value = "top2_temp_type", required = false) String top2_temp_type,
            @RequestParam(value = "top3_temp_type", required = false) String top3_temp_type,

            @RequestParam(value = "totalCoffeeCount", required = false) Integer totalCoffeeCount,
            @RequestParam(value = "matched_unique_face_ids", required = false) List<Integer> matchedUniqueFaceIds,
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

        temp_type = (temp_type != null) ? temp_type : "데이터 없음";
        top2_temp_type = (top2_temp_type != null) ? top2_temp_type : "데이터 없음";
        top3_temp_type = (top3_temp_type != null) ? top3_temp_type : "데이터 없음";

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
        model.addAttribute("temp_type",temp_type);
        model.addAttribute("top2_temp_type",top2_temp_type);
        model.addAttribute("top3_temp_type",top3_temp_type);
        model.addAttribute("top2CoffeeName", top2CoffeeName);
        model.addAttribute("top3CoffeeName", top3CoffeeName);
        model.addAttribute("totalCoffeeCount", totalCoffeeCount);

        model.addAttribute("matchedUniqueFaceIds", matchedUniqueFaceIds);

        // 배열 출력
        if (matchedUniqueFaceIds != null && !matchedUniqueFaceIds.isEmpty()) {
            System.out.println("✅ matched_unique_face_ids:");
            for (Integer id : matchedUniqueFaceIds) {
                System.out.println(" - " + id);
            }
        } else {
            System.out.println("❌ matched_unique_face_ids is empty or null");
        }

        if (matchedUniqueFaceIds != null && !matchedUniqueFaceIds.isEmpty()) {
            List<FaceWithCoffeeDTO> faceData = coffeeOrderRepository.findLatestFaceWithCoffeeDTOByFaceIds(matchedUniqueFaceIds);
            model.addAttribute("matchedFaceData", faceData);
        }


        boolean weatherApiOk = false;

        try {
            String serviceKey = "343b26b1abb17b48e2c8a95d700cbed28dcd9d5d33e0fffa9e0951c4419bc273";
            String nx = "62";
            String ny = "120";

            LocalDateTime now = LocalDateTime.now();
            String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            int hour = now.getHour();
            String baseTime;
            if (hour < 2) baseTime = "2300";
            else if (hour < 5) baseTime = "0200";
            else if (hour < 8) baseTime = "0500";
            else if (hour < 11) baseTime = "0800";
            else if (hour < 14) baseTime = "1100";
            else if (hour < 17) baseTime = "1400";
            else if (hour < 20) baseTime = "1700";
            else if (hour < 23) baseTime = "2000";
            else baseTime = "2300";

            StringBuilder urlBuilder = new StringBuilder(
                    "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst"
            );
            urlBuilder.append("?serviceKey=").append(URLEncoder.encode(serviceKey, "UTF-8"));
            urlBuilder.append("&pageNo=1");
            urlBuilder.append("&numOfRows=100");
            urlBuilder.append("&dataType=JSON");
            urlBuilder.append("&base_date=").append(baseDate);
            urlBuilder.append("&base_time=").append(baseTime);
            urlBuilder.append("&nx=").append(nx);
            urlBuilder.append("&ny=").append(ny);

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(responseCode >= 200 && responseCode <= 300 ?
                            conn.getInputStream() : conn.getErrorStream())
            );

            StringBuilder result = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            conn.disconnect();

            if (responseCode == 200) {
                weatherApiOk = true;
            }

            // HTTP 응답 확인
            if (responseCode != 200) {
                System.out.println("API 호출 실패: " + responseCode);
                System.out.println("응답 내용: " + result.toString());
                // 기본값 세팅
                model.addAttribute("tmpValue", "데이터 없음");
                model.addAttribute("ptyText", "데이터 없음");
                model.addAttribute("popValue", "데이터 없음");
                model.addAttribute("skyText", "데이터 없음");
                model.addAttribute("rehValue", "데이터 없음");
                model.addAttribute("tmx", "데이터 없음");
                model.addAttribute("tmn", "데이터 없음");
                model.addAttribute("weatherApiOk", weatherApiOk);
                return "recommendation";
            }

            // JSON 파싱
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.readValue(result.toString(), Map.class);

            List<Map<String, Object>> items = (List<Map<String, Object>>)
                    ((Map)((Map)((Map)map.get("response")).get("body")).get("items")).get("item");

            int nowHour = now.getHour();
            String closestTime = "";
            int minDiff = 9999;
            String tmpValue = "", ptyValue = "", popValue = "", skyValue = "", rehValue = "";
            String tmx = "", tmn = "";

            for (Map<String, Object> item : items) {
                String category = (String) item.get("category");
                String fcstTime = (String) item.get("fcstTime");
                int fcstHour = Integer.parseInt(fcstTime.substring(0, 2));

                if ("TMP".equals(category)) {
                    int diff = Math.abs(fcstHour - nowHour);
                    if (diff < minDiff) {
                        minDiff = diff;
                        tmpValue = (String) item.get("fcstValue");
                        closestTime = fcstTime;
                    }
                } else if ("PTY".equals(category)) {
                    ptyValue = (String) item.get("fcstValue");
                } else if ("POP".equals(category)) {
                    popValue = (String) item.get("fcstValue");
                } else if ("SKY".equals(category)) {
                    skyValue = (String) item.get("fcstValue");
                } else if ("REH".equals(category)) {
                    rehValue = (String) item.get("fcstValue");
                } else if ("TMX".equals(category) && item.get("fcstDate").equals(baseDate)) {
                    tmx = (String) item.get("fcstValue");
                } else if ("TMN".equals(category) && item.get("fcstDate").equals(baseDate)) {
                    tmn = (String) item.get("fcstValue");
                }
            }

            String skyText = switch (skyValue) {
                case "1" -> "맑음";
                case "3" -> "구름많음";
                case "4" -> "흐림";
                default -> "알수없음";
            };
            String ptyText = switch (ptyValue) {
                case "0" -> "없음";
                case "1" -> "비";
                case "2" -> "비/눈";
                case "3" -> "눈";
                case "4" -> "소나기";
                default -> "알수없음";
            };

            System.out.println("현재 시간 기준 TMP(" + closestTime + "): " + tmpValue);
            System.out.println("강수 형태(PTY): " + ptyText);
            System.out.println("강수 확률(POP): " + popValue + "%");
            System.out.println("하늘 상태(SKY): " + skyText);
            System.out.println("습도(REH): " + rehValue + "%");
            System.out.println("일 최고 기온(TMX): " + tmx);
            System.out.println("일 최저 기온(TMN): " + tmn);

            // 모델에 넣기
            model.addAttribute("tmpValue", tmpValue);
            model.addAttribute("ptyText", ptyText);
            model.addAttribute("popValue", popValue);
            model.addAttribute("skyText", skyText);
            model.addAttribute("rehValue", rehValue);
            model.addAttribute("tmx", tmx);
            model.addAttribute("tmn", tmn);
            model.addAttribute("weatherApiOk", weatherApiOk);

        } catch (Exception e) {
            e.printStackTrace();
            // 예외 발생 시 기본값 세팅
            model.addAttribute("tmpValue", "데이터 없음");
            model.addAttribute("ptyText", "데이터 없음");
            model.addAttribute("popValue", "데이터 없음");
            model.addAttribute("skyText", "데이터 없음");
            model.addAttribute("rehValue", "데이터 없음");
            model.addAttribute("tmx", "데이터 없음");
            model.addAttribute("tmn", "데이터 없음");
            model.addAttribute("weatherApiOk", weatherApiOk);
        }




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
            @RequestParam(value = "temp_type_1", required = false) String tempType1,

            @RequestParam(value = "face_id_2", required = false) String faceId2Str,
            @RequestParam(value = "image_path_2", required = false) String imagePath2,
            @RequestParam(value = "coffee_id_2", required = false) String coffeeId2Str,
            @RequestParam(value = "coffee_name_2", required = false) String coffeeName2,
            @RequestParam(value = "coffee_image_url_2", required = false) String coffeeImageUrl2,
            @RequestParam(value = "latest_order_2", required = false) String latestOrder2,
            @RequestParam(value = "temp_type_2", required = false) String tempType2,

            @RequestParam(value = "face_id_3", required = false) String faceId3Str,
            @RequestParam(value = "image_path_3", required = false) String imagePath3,
            @RequestParam(value = "coffee_id_3", required = false) String coffeeId3Str,
            @RequestParam(value = "coffee_name_3", required = false) String coffeeName3,
            @RequestParam(value = "coffee_image_url_3", required = false) String coffeeImageUrl3,
            @RequestParam(value = "latest_order_3", required = false) String latestOrder3,
            @RequestParam(value = "temp_type_3", required = false) String tempType3,
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

        // Model에 temp_type도 추가
        model.addAttribute("temp_type1", tempType1 != null ? tempType1 : "HOT");
        model.addAttribute("temp_type2", tempType2 != null ? tempType2 : "HOT");
        model.addAttribute("temp_type3", tempType3 != null ? tempType3 : "HOT");

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
        } catch (ParseException e){
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
            String tempType = (String) item.get("tempType");

            // CoffeeOrderService로 주문 저장
            coffeeOrderService.saveOrderMultipleTimes(faceId, coffeeId, quantity, tempType);
        }

        return ResponseEntity.ok("주문 저장 완료");
    }

    /*
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
    */

    @PostMapping("/pay_single")
    public ResponseEntity<String> paySingle(
            @RequestParam("faceId") Long faceId,
            @RequestParam("coffeeId") Long coffeeId,
            @RequestParam("tempType") String tempType) {

        System.out.println(faceId);
        System.out.println(coffeeId);
        System.out.println("tempType: " + tempType);

        coffeeOrderService.saveOrder(faceId, coffeeId,tempType);
        return ResponseEntity.ok("결제 및 저장 완료!");
    }

    @GetMapping("/load")
    public String showLoadPage() {
        return "load"; // load.mustache
    }




}
