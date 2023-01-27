package com.gogi1000.datecourse.service.datecourse;

import com.gogi1000.datecourse.common.CamelHashMap;
import com.gogi1000.datecourse.dto.DatecourseDTO;
import com.gogi1000.datecourse.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DatecourseService {
    // 데이트 코스 등록_세혁
    void insertDatecourse(Datecourse datecourse, List<DatecourseHours> iDatecourseHoursList,
                          List<DatecourseMenu> iDatecourseMenuList, List<DatecourseImage> uploadImageList);

    // 데이트 코스 리스트 화면(관리자) 조회 - 페이지 번호 추가_세혁
    Page<Datecourse> getPageDatecourseList(Datecourse datecourse, Pageable pageable);

    // 데이트 코스 리스트 화면(관리자)에서 삭제 시, 데이트 코스 사용여부를 ('Y' -> 'N')으로 업데이트_세혁
    void updateDatecourseList(List<Integer> updateDatecourseList);

    // 데이트 코스 상세 조회_세혁
    Datecourse getDatecourse(int datecourseNo);

    // 데이트 코스 영업시간 조회_세혁
    List<DatecourseHours> getDatecourseHoursList(int datecourseNo);

    // 데이트 코스 메뉴 조회_세혁
    List<DatecourseMenu> getDatecourseMenuList(int datecourseNo);

    // 데이트 코스 이미지 조회_세혁
    List<DatecourseImage> getDatecourseImageList(int datecourseNo);

    // 데이트 코스 수정_세혁
    Datecourse updateDatecourse(Datecourse datecourse,
                                List<DatecourseHours> uDatecourseHoursList,
                                List<DatecourseMenu> uDatecourseMenuList,
                                List<DatecourseImage> uImageList);

    // 카테고리 선택에 따른 데이트 코스 조회_세혁
    Page<CamelHashMap> getPageCateDatecourseList(DatecourseDTO datecourseDTO, Pageable pageable, CustomUserDetails customUser);

    // 데이트 코스 관련 데이터 삭제_세혁
    void deleteDatecourse(int datecourseNo);
}