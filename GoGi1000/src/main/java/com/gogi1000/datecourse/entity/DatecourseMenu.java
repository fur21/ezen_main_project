package com.gogi1000.datecourse.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="T_GGC_DATECOURSE_MENU")
@Data
@IdClass(DatecourseMenuId.class) // 복합키의 경우, 복합키를 가지고 있는 클래스 만들어 준다.
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // 모든 매개변수를 받는 생성자
@Builder // 객체 생성
//@DynamicInsert // insert 구문이 생성될 때 null 값인 컬럼은 배제하고 구문 생성
@DynamicUpdate // update 변경되지 않은 값들을 제외하고 구문 생성
public class DatecourseMenu {
    @Id
    private int datecourseNo;
    @Id
    private int datecourseMenuNo;

    @Column(nullable = false)
    private String datecourseMenuType;

    @Column(nullable = false)
    private String datecourseMenuNm;

    @Column(nullable = false)
    private int datecourseMenuPrice;

    @Column(nullable = false)
    private LocalDateTime datecourseMenuRgstDate;

    @Column(nullable = false)
    private LocalDateTime datecourseMenuModfDate;

    @Column(nullable = false)
    private String datecourseMenuUseYn;

    @Transient
    private String datecourseMenuStatus;

    @Transient
    private String newDatecourseMenuNm;

    @Transient
    private int newDatecourseMenuPrice;
}
