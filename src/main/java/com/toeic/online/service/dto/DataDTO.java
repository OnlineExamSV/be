package com.toeic.online.service.dto;

import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataDTO extends ExportDTO implements Serializable {
    private String parentCode;

    private String currentYear;
    private String currentClassCode;
    private Map<String, String> dayAndCheckDateOfMonth;
    private List<String> studentCodes;
    private String fromDate;
    private String toDate;
    // data grid server client need save
    private String month;
    private String years;
    private String semester;
    private String titleHead;
    //
    private long currentPage;
    private long pageSize;
    private long totalRecord;

    private String studentCode;
    private String studentName;
    private List<String> holidays;
    private int totalCount;
    private int totalRestByReason;
    private int totalRestNoReason;
    private int totalGoingSchool;
}
