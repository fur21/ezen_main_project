package com.gogi1000.datecourse.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogi1000.datecourse.common.CamelHashMap;
import com.gogi1000.datecourse.common.FileUtils;
import com.gogi1000.datecourse.common.FileUtilsForMap;
import com.gogi1000.datecourse.dto.DatecourseImageDTO;
import com.gogi1000.datecourse.dto.HotdealDTO;
import com.gogi1000.datecourse.dto.ResponseDTO;
import com.gogi1000.datecourse.entity.CustomUserDetails;
import com.gogi1000.datecourse.entity.DatecourseImage;
import com.gogi1000.datecourse.entity.Hotdeal;
import com.gogi1000.datecourse.service.hotdeal.HotdealService;

@RestController
@RequestMapping("/hotdeal")
public class HotdealController {
	@Autowired
	private HotdealService hotdealService;
    
	//핫딜 입력페이지 이동_김도원
	@GetMapping("/insertHotdeal")
	public ModelAndView insertHotdealView() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("admin/insertHotdeal.html");
		
		return mv;
	}
	
	//핫딜 입력_김도원
	@PostMapping("/insertHotdeal")
	public void insertHotdeal(HotdealDTO hotdealDTO, MultipartFile[] uploadFiles, 
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		//핫딜
		Hotdeal hotdeal = Hotdeal.builder()
								 .hotdealNm(hotdealDTO.getHotdealNm())
								 .hotdealEndDate(hotdealDTO.getHotdealEndDate())
								 .hotdealOfficialSite(hotdealDTO.getHotdealOfficialSite())
								 .hotdealPrice(hotdealDTO.getHotdealPrice())
								 .hotdealSalerate(hotdealDTO.getHotdealSalerate())
								 .hotdealSeller(hotdealDTO.getHotdealSeller())
								 .hotdealSummary(hotdealDTO.getHotdealSummary())
								 .hotdealTel(hotdealDTO.getHotdealTel())
								 .hotdealDesc(hotdealDTO.getHotdealDesc())
								 .hotdealRgstDate(LocalDateTime.now())
								 .hotdealModfDate(LocalDateTime.now())
								 .hotdealUseYn("Y")
								 .build();
		
		//DB에 입력될 이미지파일 데이터 리스트
		List<DatecourseImage> uploadImageList = new ArrayList<DatecourseImage>();
		
		
		if(uploadFiles.length > 0) {
			//파일경로
			String attachPath = request.getSession().getServletContext().getRealPath("/")
					+ "/upload/";
			
			//디렉토리가 없으면 폴더 생성
			File directory = new File(attachPath);
			
			if(!directory.exists()) {
				directory.mkdir();
			}
		
			for(int i=0; i < uploadFiles.length; i++) {
				MultipartFile file = uploadFiles[i];
				
				//파일 이름이 있을때만 배열에 담기
				if(!file.getOriginalFilename().equals("") &&
				file.getOriginalFilename() != null) {
					DatecourseImage datecourseImage = new DatecourseImage();
					
					datecourseImage = FileUtils.parseFileInfo(file, attachPath);
					uploadImageList.add(datecourseImage);
				}
			}
		}
		
		hotdealService.insertHotdeal(hotdeal, uploadImageList);
		
		response.sendRedirect("/hotdeal/hotdealList");
	}
	
	
	//핫딜 리스트 조회화면(관리자)_김도원
    @GetMapping("/hotdealList")
    public ModelAndView getHotdealList(HotdealDTO hotdealDTO,
    		@PageableDefault(page=0, size=13) Pageable pageable) {
    	
    	Hotdeal hotdeal = Hotdeal.builder()
    							 .searchCondition(hotdealDTO.getSearchCondition())
    							 .searchKeyword(hotdealDTO.getSearchKeyword())
    							 .build();
    	
    	//List<Hotdeal> hotdealList = hotdealService.getHotdealList(hotdeal);
    	
    	Page<Hotdeal> pageHotdealList = hotdealService.getPageHotdealList(hotdeal, pageable);
    	
    	Page<HotdealDTO> pageHotdealDTOList = pageHotdealList.map(pageHotdeal ->
    															  HotdealDTO.builder()
    															  			.hotdealNo(pageHotdeal.getHotdealNo())
    															  			.hotdealNm(pageHotdeal.getHotdealNm())
    															  			.hotdealOfficialSite(pageHotdeal.getHotdealOfficialSite())
    															  			.hotdealPrice(pageHotdeal.getHotdealPrice())
    						    											.hotdealSalerate(pageHotdeal.getHotdealSalerate())
    															  			.hotdealTel(pageHotdeal.getHotdealTel())
    															  			.hotdealDesc(pageHotdeal.getHotdealDesc())
    															  			.hotdealRgstDate(pageHotdeal.getHotdealRgstDate() == null ?
    															  					null : pageHotdeal.getHotdealRgstDate().toString())
    															  			.hotdealEndDate(pageHotdeal.getHotdealEndDate())
    															  			.hotdealModfDate(pageHotdeal.getHotdealModfDate() == null ?
    															  					null : pageHotdeal.getHotdealModfDate().toString())
    															  			.hotdealUseYn(pageHotdeal.getHotdealUseYn())
    															  			.build()
    															  			);
    															  			
    	ModelAndView mv = new ModelAndView();
    	mv.setViewName("admin/getHotdealList.html");
    	mv.addObject("getHotdealList", pageHotdealDTOList);
    	
    	if(hotdealDTO.getSearchCondition() != null && !hotdealDTO.getSearchCondition().equals("")) {
    		mv.addObject("searchCondition", hotdealDTO.getSearchCondition());
    	}
    	if(hotdealDTO.getSearchKeyword() != null && !hotdealDTO.getSearchKeyword().equals("")) {
    		mv.addObject("hotdealSearchKeyword", hotdealDTO.getSearchKeyword());
    	}
    	if(hotdealDTO.getSelectOption() != null && !hotdealDTO.getSelectOption().equals("")) {
    		mv.addObject("selectOption", hotdealDTO.getSelectOption());
    	}
    	return mv;
    	
    }
    //핫딜 리스트 조회페이지에서 게시글 삭제시 UseYn을 ('Y' => 'N')으로 변경_김도원
    @PostMapping("/updateHotdealUseYn")
    public ResponseEntity<?> updateHotdealUseYn(HotdealDTO hotdealDTO, Pageable pageable, HttpServletRequest request, HttpServletResponse response,
    		@RequestParam("valueArr") String valueArr) throws IOException{
    	
    	ResponseDTO<HotdealDTO> responseDTO = new ResponseDTO<HotdealDTO>();
    	try {
	    	List<Integer> valueArrList = new ObjectMapper().readValue(valueArr,
	    									new TypeReference<List<Integer>>() {});
	    	
	    	//업데이트
	    	for(int i=0; i < valueArrList.size(); i++) {
	    		hotdealService.updateHotdealUseYn(valueArrList.get(i));
	    	}
	    	
	    	Hotdeal hotdeal = Hotdeal.builder()
	    							 .searchCondition(hotdealDTO.getSearchCondition())
	    							 .searchKeyword(hotdealDTO.getSearchKeyword())
	    							 .build();
	    	
	    	//변경내용으로 조회
	    	Page<Hotdeal> pageHotdealList = hotdealService.getPageHotdealList(hotdeal, pageable);
	    	
	    	Page<HotdealDTO> pageHotdealDTOList = pageHotdealList.map(pageHotdeal ->
																    	HotdealDTO.builder()
															  			.hotdealNo(pageHotdeal.getHotdealNo())
															  			.hotdealNm(pageHotdeal.getHotdealNm())
															  			.hotdealOfficialSite(pageHotdeal.getHotdealOfficialSite())
															  			.hotdealPrice(pageHotdeal.getHotdealPrice())
																		.hotdealSalerate(pageHotdeal.getHotdealSalerate())
															  			.hotdealTel(pageHotdeal.getHotdealTel())
															  			.hotdealDesc(pageHotdeal.getHotdealDesc())
															  			.hotdealRgstDate(pageHotdeal.getHotdealRgstDate() == null ?
															  					null : pageHotdeal.getHotdealRgstDate().toString())
															  			.hotdealEndDate(pageHotdeal.getHotdealEndDate())
															  			.hotdealModfDate(pageHotdeal.getHotdealModfDate() == null ?
															  					null : pageHotdeal.getHotdealModfDate().toString())
															  			.hotdealUseYn(pageHotdeal.getHotdealUseYn())
															  			.build()
															  			);
	    	responseDTO.setPageItems(pageHotdealDTOList);
	    	return ResponseEntity.ok().body(responseDTO);
	    	
    	} catch(Exception e) {
    		responseDTO.setErrorMessage(e.getMessage());
    		
    		return ResponseEntity.badRequest().body(responseDTO);
    	}
    }
    //핫딜 상세조회 페이지_김도원
    @GetMapping("getHotdeal/{hotdealNo}")
    public ModelAndView getHotdeal(@PathVariable int hotdealNo ) {
    	
    	
    	//핫딜 조회
    	Hotdeal hotdeal = hotdealService.getHotdeal(hotdealNo);
    	
    	//조회 내용 담아주기
    	HotdealDTO hotdealDTO = HotdealDTO.builder()
    									  .hotdealNo(hotdeal.getHotdealNo())
    									  .hotdealNm(hotdeal.getHotdealNm())
    									  .hotdealSummary(hotdeal.getHotdealSummary())
    									  .hotdealEndDate(hotdeal.getHotdealEndDate())
    									  .hotdealSalerate(hotdeal.getHotdealSalerate())
    									  .hotdealSeller(hotdeal.getHotdealSeller())
    									  .hotdealTel(hotdeal.getHotdealTel())
    									  .hotdealPrice(hotdeal.getHotdealPrice())
    									  .hotdealOfficialSite(hotdeal.getHotdealOfficialSite())
    									  .hotdealDesc(hotdeal.getHotdealDesc())
    									  .hotdealUseYn(hotdeal.getHotdealUseYn())
    									  .build();
    	
    	//이미지 목록 조회
    	List<DatecourseImage> datecourseImageList = hotdealService.getHotdealImageList(hotdealNo);
    	
    	List<DatecourseImageDTO> datecourseImageDTOList = new ArrayList<>();
    	
    	//이미지 목록 담아주기
    	for(DatecourseImage datecourseImage : datecourseImageList) {
    		DatecourseImageDTO datecourseImageDTO = DatecourseImageDTO.builder()
    																  .imageGroup(datecourseImage.getImageGroup())
    																  .referenceNo(datecourseImage.getReferenceNo())
    																  .imageNo(datecourseImage.getImageNo())
    																  .imageNm(datecourseImage.getImageNm())
    																  .imageOriginNm(datecourseImage.getImageOriginNm())
    																  .imageExt(datecourseImage.getImageExt())
    																  .imagePath(datecourseImage.getImagePath())
    																  .build();
    		datecourseImageDTOList.add(datecourseImageDTO);	
    		System.out.println(datecourseImageDTOList);
    	}
    	ModelAndView mv = new ModelAndView();
    	mv.setViewName("hotdeal/getHotdealDetail.html");
    	mv.addObject("getHotdeal", hotdealDTO);
    	mv.addObject("getDatecourseImageList", datecourseImageDTOList);
    	System.out.println(mv);
 		return mv;			  
    	}
    
    
	//핫딜 조회 및 수정 페이지(관리자) 이동_김도원
	@GetMapping("updateHotdeal/{hotdealNo}")
	public ModelAndView updateHotdeal(@PathVariable int hotdealNo) {
		
		//핫딜 조회
		Hotdeal hotdeal = hotdealService.getHotdeal(hotdealNo);
		
		HotdealDTO hotdealDTO = HotdealDTO.builder()
										  .hotdealNo(hotdealNo)
										  .hotdealNm(hotdeal.getHotdealNm())
										  .hotdealEndDate(hotdeal.getHotdealEndDate())
										  .hotdealOfficialSite(hotdeal.getHotdealOfficialSite())
										  .hotdealPrice(hotdeal.getHotdealPrice())
										  .hotdealSalerate(hotdeal.getHotdealSalerate())
										  .hotdealSeller(hotdeal.getHotdealSeller())
										  .hotdealSummary(hotdeal.getHotdealSummary())
										  .hotdealTel(hotdeal.getHotdealTel())
										  .hotdealDesc(hotdeal.getHotdealDesc())
										  .hotdealRgstDate(hotdeal.getHotdealRgstDate() == null ?
													 null: hotdeal.getHotdealRgstDate().toString())
										  .hotdealUseYn(hotdeal.getHotdealUseYn())
										  .build();
		
		
		//이미지 목록 조회
        List<DatecourseImage> datecourseImageList = hotdealService.getHotdealImageList(hotdealNo);
    	
    	List<DatecourseImageDTO> datecourseImageDTOList = new ArrayList<>();
    	 
    	for(DatecourseImage datecourseImage : datecourseImageList) {
    		DatecourseImageDTO datecourseImageListDTO = DatecourseImageDTO.builder()
    																  .imageGroup(datecourseImage.getImageGroup())
    																  .referenceNo(datecourseImage.getReferenceNo())
    																  .imageNo(datecourseImage.getImageNo())
    																  .imageNm(datecourseImage.getImageNm())
    																  .imageOriginNm(datecourseImage.getImageOriginNm())
    																  .imageExt(datecourseImage.getImageExt())
    																  .imagePath(datecourseImage.getImagePath())
    																  .imageRgstDate(datecourseImage.getImageRgstDate().toString())
    																  .build();
    		datecourseImageDTOList.add(datecourseImageListDTO);	
    		System.out.println("datecourseImageList" + datecourseImageDTOList);
    	}
		
		
									
		ModelAndView mv = new ModelAndView();
		
		mv.setViewName("admin/getHotdeal.html");
		mv.addObject("getHotdeal", hotdealDTO);
		mv.addObject("datecourseImageList", datecourseImageDTOList);
	
		return mv;
	}
	
	//핫딜 수정(관리자)_김도원
	@Transactional
	@PutMapping("/updateHotdeal")
	public ResponseEntity<?> updateHotdeal(HotdealDTO hotdealDTO, HttpServletResponse response,
			HttpServletRequest request, MultipartFile[] uploadFiles, MultipartFile[] changedFiles, @RequestParam("originFiles") String originFiles) throws IOException {
		
		ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>();
		
		List<Map<String, Object>> originFileList = new ObjectMapper().readValue(originFiles,
													new TypeReference<List<Map<String, Object>>>() {});
		
		String attachPath = request.getSession().getServletContext().getRealPath("/") +
				"/upload/";
		
		File directory = new File(attachPath);
		
		if(!directory.exists()) {
			directory.mkdir();
		}
		
		//DB에서 수정, 삭제, 추가 될 파일 정보를 담는 리스트
		List<CamelHashMap> uFileList = new ArrayList<CamelHashMap>();
		
		try {
			Hotdeal hotdeal = Hotdeal.builder()
							   .hotdealNo(hotdealDTO.getHotdealNo())
							   .hotdealNm(hotdealDTO.getHotdealNm())
							   .hotdealEndDate(hotdealDTO.getHotdealEndDate())
							   .hotdealSeller(hotdealDTO.getHotdealSeller())
							   .hotdealOfficialSite(hotdealDTO.getHotdealOfficialSite())
							   .hotdealPrice(hotdealDTO.getHotdealPrice())
							   .hotdealSalerate(hotdealDTO.getHotdealSalerate())
							   .hotdealTel(hotdealDTO.getHotdealTel())
							   .hotdealSummary(hotdealDTO.getHotdealSummary())
							   .hotdealDesc(hotdealDTO.getHotdealDesc())
							   .hotdealRgstDate(LocalDateTime.parse(hotdealDTO.getHotdealRgstDate()))
							   .hotdealModfDate(LocalDateTime.now())
							   .hotdealUseYn(hotdealDTO.getHotdealUseYn())
							   .build();
			
			
			System.out.println(hotdeal);
			//파일 처리
			for(int i = 0; i < originFileList.size(); i++) {
				//수정되는 파일 처리
				if(originFileList.get(i).get("imageFileStatus").equals("U")) {
					for(int j = 0; j < changedFiles.length; j++) {
						if(originFileList.get(i).get("newFileNm").equals(
								changedFiles[j].getOriginalFilename())) {
							CamelHashMap datecourseImageMap = new CamelHashMap();
							
							MultipartFile file = changedFiles[j];
							
							datecourseImageMap = FileUtilsForMap.parseFileInfo(file, attachPath);
							
							datecourseImageMap.put("image_Group", "E0002");
							datecourseImageMap.put("image_No",originFileList.get(i).get("imageNo"));
							datecourseImageMap.put("img_File_Status", "U");
							System.out.println("udatecourseImageMap" + datecourseImageMap);
							
							
							uFileList.add(datecourseImageMap);
						}
					}
				
				//삭제되는 파일 처리
				} else if(originFileList.get(i).get("imageFileStatus").equals("D")) {
					CamelHashMap datecourseImageMap = new CamelHashMap();
					
					System.out.println("originFileList" + originFileList);
					
					datecourseImageMap.put("image_Group", "E0002");
					datecourseImageMap.put("reference_No", originFileList.get(i).get("referenceNo"));
					datecourseImageMap.put("image_No", originFileList.get(i).get("imageNo"));
					datecourseImageMap.put("img_File_Status", "D");
					
					System.out.println("ddatecourseImageMap" + datecourseImageMap);
					//실제 파일 삭제
					File dFile = new File(attachPath + originFileList.get(i).get("imageNm"));
					dFile.delete();
					
					uFileList.add(datecourseImageMap);
				}
			}
			
			//추가된 파일 처리
			if(uploadFiles.length > 0) {
				for(int i = 0; i < uploadFiles.length; i++) {
					MultipartFile file = uploadFiles[i];
					if(file.getOriginalFilename() != null &&
						!file.getOriginalFilename().equals("")) {
						CamelHashMap datecourseImageMap = new CamelHashMap();
						
						datecourseImageMap = FileUtilsForMap.parseFileInfo(file, attachPath);
						
						datecourseImageMap.put("image_Group", "E0002");
						datecourseImageMap.put("reference_No", hotdeal.getHotdealNo());
						datecourseImageMap.put("img_File_Status", "I");
						
						System.out.println("idatecourseImageMap" + datecourseImageMap);
						
						uFileList.add(datecourseImageMap);
					}
				}
			}

			hotdealService.updateHotdeal(hotdeal, uFileList);
			
			hotdeal = hotdealService.getHotdeal(hotdealDTO.getHotdealNo());
			
			HotdealDTO returnHotdeal = HotdealDTO.builder()
										   .hotdealNo(hotdeal.getHotdealNo())
										   .hotdealNm(hotdeal.getHotdealNm())
										   .hotdealEndDate(hotdeal.getHotdealEndDate())
										   .hotdealOfficialSite(hotdeal.getHotdealOfficialSite())
										   .hotdealPrice(hotdeal.getHotdealPrice())
										   .hotdealSalerate(hotdeal.getHotdealSalerate())
										   .hotdealSeller(hotdeal.getHotdealSeller())
										   .hotdealTel(hotdeal.getHotdealTel())
										   .hotdealSummary(hotdeal.getHotdealSummary())
										   .hotdealDesc(hotdeal.getHotdealDesc())
										   .hotdealRgstDate(hotdeal.getHotdealRgstDate() == null ?
												   		null :
													   	hotdeal.getHotdealRgstDate().toString())
										   .build();
			
			List<DatecourseImage> datecourseImageList = hotdealService.getHotdealImageList(hotdeal.getHotdealNo());

			List<DatecourseImageDTO> datecourseImageDTOList = new ArrayList<DatecourseImageDTO>();
			
			for(DatecourseImage datecourseImage : datecourseImageList) {
				DatecourseImageDTO datecourseImageDTO = DatecourseImageDTO.builder()
																		  .imageGroup(datecourseImage.getImageGroup())
																		  .referenceNo(datecourseImage.getReferenceNo())
																		  .imageNo(datecourseImage.getImageNo())
																		  .imageNm(datecourseImage.getImageNm())
																		  .imageOriginNm(datecourseImage.getImageOriginNm())
																		  .imageExt(datecourseImage.getImageExt())
																		  .imagePath(datecourseImage.getImagePath())
																		  .imageRgstDate(datecourseImage.getImageRgstDate().toString())
																		  .build();
				System.out.println(datecourseImageDTO);
				datecourseImageDTOList.add(datecourseImageDTO);
			}
			
			Map<String, Object> returnMap = new HashMap<String, Object>();
			
			
			System.out.println(returnHotdeal);
			returnMap.put("getHotdeal", returnHotdeal);
			returnMap.put("datecourseImageList", datecourseImageDTOList);
			
			responseDTO.setItem(returnMap);
			
			return ResponseEntity.ok().body(responseDTO);			
		} catch(Exception e) {
			responseDTO.setErrorMessage(e.getMessage());
			
			return ResponseEntity.badRequest().body(responseDTO);	
		}
	}
	
    @GetMapping("/selectHotdealList")
    public ResponseEntity<?> selectHotdealList(HotdealDTO hotdealDTO, 
    		@PageableDefault(page=0, size=13) Pageable pageable) throws IOException {
    	
    	ResponseDTO<Map<String, Object>> response = new ResponseDTO<>();
    	
    	System.out.println(hotdealDTO);
    	
    	try {
    		Hotdeal hotdeal = Hotdeal.builder()
    								 .searchCondition(hotdealDTO.getSearchCondition())
    							  	 .searchKeyword(hotdealDTO.getSearchKeyword())
    							  	 .selectOption(hotdealDTO.getSelectOption())
    							     .build();
    		
    		
    		Page<Hotdeal> selectPageHotdealList = hotdealService.getSelectPageHotdealList(hotdeal, pageable);
	    	
	    	Page<HotdealDTO> pageHotdealDTOList = selectPageHotdealList.map(pageHotdeal ->
																    	HotdealDTO.builder()
															  			.hotdealNo(pageHotdeal.getHotdealNo())
															  			.hotdealNm(pageHotdeal.getHotdealNm())
															  			.hotdealOfficialSite(pageHotdeal.getHotdealOfficialSite())
															  			.hotdealPrice(pageHotdeal.getHotdealPrice())
																		.hotdealSalerate(pageHotdeal.getHotdealSalerate())
															  			.hotdealTel(pageHotdeal.getHotdealTel())
															  			.hotdealDesc(pageHotdeal.getHotdealDesc())
															  			.hotdealRgstDate(pageHotdeal.getHotdealRgstDate() == null ?
															  					null : pageHotdeal.getHotdealRgstDate().toString())
															  			.hotdealEndDate(pageHotdeal.getHotdealEndDate())
															  			.hotdealModfDate(pageHotdeal.getHotdealModfDate() == null ?
															  					null : pageHotdeal.getHotdealModfDate().toString())
															  			.hotdealUseYn(pageHotdeal.getHotdealUseYn())
															  			.build()
															  			);
    		
	    	 Map<String, Object> returnMap = new HashMap<String, Object>();

	         returnMap.put("selectHotdealList", pageHotdealDTOList);
	         returnMap.put("selectHotdeal", hotdeal);
	         System.out.println(returnMap);
	         response.setItem(returnMap);
	         System.out.println(response);
	         return ResponseEntity.ok().body(response);
    	}
    	catch(Exception e) {
    		response.setErrorMessage(e.getMessage());
			System.out.println(response);
			return ResponseEntity.badRequest().body(response);
    	}
    	

    }
}


