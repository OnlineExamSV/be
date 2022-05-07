package com.toeic.online.service.dto;

public class ExcelDynamicDTO {

    public long id;
    public String type;
    public String code;
    public String name;
    public String value;
    public String editAble;
    public String optionValue;
    public int ord;
    public String lineError;
    public String columnError;
    public String detailError;

    public String getLineError() {
        return lineError;
    }

    public void setLineError(String lineError) {
        this.lineError = lineError;
    }

    public String getColumnError() {
        return columnError;
    }

    public void setColumnError(String columnError) {
        this.columnError = columnError;
    }

    public String getDetailError() {
        return detailError;
    }

    public void setDetailError(String detailError) {
        this.detailError = detailError;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrd() {
        return ord;
    }

    public void setOrd(int ord) {
        this.ord = ord;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEditAble() {
        return editAble;
    }

    public void setEditAble(String editAble) {
        this.editAble = editAble;
    }

    public String getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
