package com.toeic.online.service.dto;

import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SheetConfigDTO {

    private List<?> list;
    private String[] headers;
    private List<CellConfigDTO> cellConfigList;
    private List<CellConfigDTO> cellCustomList;
    private String sheetName;
    private boolean hasIndex = true;
    private int rowStart = 0;
    private boolean hasBorder;
    private int exportType;
    private List<DataDTO> dataDTOs;
}
