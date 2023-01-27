package com.gogi1000.datecourse.service.main.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.gogi1000.datecourse.common.CamelHashMap;
import com.gogi1000.datecourse.entity.CustomUserDetails;
import com.gogi1000.datecourse.entity.Datecourse;
import com.gogi1000.datecourse.entity.DatecourseImage;
import com.gogi1000.datecourse.entity.Hotdeal;
import com.gogi1000.datecourse.repository.DatecourseHoursRepository;
import com.gogi1000.datecourse.repository.DatecourseImageRepository;
import com.gogi1000.datecourse.repository.DatecourseRepository;
import com.gogi1000.datecourse.repository.HotdealRepository;
import com.gogi1000.datecourse.service.main.MainService;

@Service
public class MainServiceImpl implements MainService{
	
	@Autowired
	private DatecourseRepository datecourseRepository;
	
	@Autowired
	private DatecourseHoursRepository datecourseHoursRepository;
	
	@Autowired 
	private HotdealRepository hotdealRepository;
	
	@Autowired
	private DatecourseImageRepository datecourseImageRepository;
	
	// 검색창에서 지역명, 코스명, 내용으로 검색 후 조회
	@Override
	public Page<CamelHashMap> getSearchMapDatecourseList(Datecourse datecourse, Pageable pageable,
			CustomUserDetails customUser) {
		if(datecourse.getSearchKeyword() != null && !datecourse.getSearchKeyword().equals("")) {
			if(customUser != null) {
				return datecourseRepository.getSearchDatecourseList(
						datecourse.getSearchKeyword(), 
						datecourse.getSearchKeyword(), 
						datecourse.getSearchKeyword(), 
						pageable, customUser);
			} else {
				System.out.println("test");
				return datecourseRepository.getSearchDatecourseList(datecourse.getSearchKeyword(), 
																	datecourse.getSearchKeyword(), 
																	datecourse.getSearchKeyword(), 
																	pageable);
			}
		} else if (datecourse.getDatecourseArea() != null && !datecourse.getDatecourseArea().equals("")) {
			if(customUser != null) {
				return datecourseRepository.getMapDatecourseList(datecourse.getDatecourseArea(), pageable, customUser);
			} else {
				return datecourseRepository.getMapDatecourseList(datecourse.getDatecourseArea(), pageable);
			}
		} else {
			return null;
		}
	}
	
	// 메인에서 인기 랭킹 리스트 조회
	@Override
	public List<CamelHashMap> getRankDatecourseList() {
		return datecourseRepository.getRankDatecourseList();
	}
	
	// 메인에서 핫딜 리스트 조회
	@Override
	public List<CamelHashMap> getHotdealDatecourseList() {
		return hotdealRepository.getHotdealDatecourseList();
	}
	
	// 메인에서 인기 상세 페이지 조회 시, 조회수 증가_인겸
	@Override
	public void updateCateDatecourseCnt(int datecourseNo) {
		datecourseRepository.updateCateDatecourseCnt(datecourseNo);
	}
	
	// 메인에서 인기 상세 페이지 조회 _인겸
	@Override
	public Datecourse getCateDatecourse(int datecourseNo) {
		return datecourseRepository.getCateDatecourse(datecourseNo);
	}
	
	// 메인에서 인기 상세 페이지 조회 시, 데이트 코스 메뉴, 가격 조회
	@Override
	public List<CamelHashMap> getCateDatecourseMenu(int datecourseNo) {
		return datecourseRepository.getCateDatecourseMenu(datecourseNo);
	}
	
	// 메인에서 인기 상세 페이지 조회 시, 영업 시간 리스트 조회_인겸
	@Override
	public List<CamelHashMap> getCateDatecourseHours(int datecourseNo) {
		return datecourseHoursRepository.getCateDatecourseHours(datecourseNo);
	}
	
	// 메인에서 인기 페이지 조회 시, 이미지 리스트 조회_인겸
	public List<DatecourseImage> getDatecourseImageList(int datecourseNo) {
		return datecourseImageRepository.findByDatecourse(datecourseNo);
	}
	
	// 메인에서 핫딜 상세 페이지 조회
	@Override
	public Hotdeal getHotdeal(int hotdealNo) {
		return hotdealRepository.findById(hotdealNo).get();
	}
	
	// 핫딜 상세 페이지에서 이미지 조회
	@Override
	public List<DatecourseImage> getDatecourseHotdealImageList(int hotdealNo) {
		Hotdeal hotdeal = Hotdeal.builder()
							     .hotdealNo(hotdealNo)
							     .build();
		
		return datecourseImageRepository.findByHotdeal(hotdeal);
	}
	
}
