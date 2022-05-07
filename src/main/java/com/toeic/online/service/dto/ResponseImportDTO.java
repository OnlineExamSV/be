package com.toeic.online.service.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseImportDTO {

    private Integer numberErrors;
    private Integer numberSuccess;
    private Integer total;
    private List<?> listSuccess = new ArrayList<>();
    private List<?> listErrors = new ArrayList<>();

    public ResponseImportDTO(Integer numberErrors, Integer numberSuccess, Integer total) {
        this.numberErrors = numberErrors;
        this.numberSuccess = numberSuccess;
        this.total = total;
    }
}
