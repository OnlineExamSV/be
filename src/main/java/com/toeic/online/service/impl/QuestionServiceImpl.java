package com.toeic.online.service.impl;

import com.toeic.online.commons.ExportUtils;
import com.toeic.online.commons.FileExportUtil;
import com.toeic.online.constant.AppConstants;
import com.toeic.online.domain.*;
import com.toeic.online.repository.ChoiceRepository;
import com.toeic.online.repository.QuestionRepository;
import com.toeic.online.repository.TypeQuestionRepository;
import com.toeic.online.repository.impl.QuestionRepositoryCustomImpl;
import com.toeic.online.service.QuestionService;
import com.toeic.online.service.UserService;
import com.toeic.online.service.dto.*;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepositoryCustomImpl questionRepositoryCustomImpl;

    private final ChoiceRepository choiceRepository;

    private final QuestionRepository questionRepository;

    private final ExportUtils exportUtils;

    private final FileExportUtil fileExportUtil;

    private final UserService userService;

    private final TypeQuestionRepository typeQuestionRepository;

    List<ExcelDynamicDTO> lstError;

    private static final String SHEETNAME = "question.sheetName";

    public QuestionServiceImpl(
        QuestionRepositoryCustomImpl questionRepositoryCustomImpl,
        ChoiceRepository choiceRepository,
        QuestionRepository questionRepository,
        ExportUtils exportUtils,
        FileExportUtil fileExportUtil,
        UserService userService,
        TypeQuestionRepository typeQuestionRepository
    ) {
        this.questionRepositoryCustomImpl = questionRepositoryCustomImpl;
        this.choiceRepository = choiceRepository;
        this.questionRepository = questionRepository;
        this.exportUtils = exportUtils;
        this.fileExportUtil = fileExportUtil;
        this.userService = userService;
        this.typeQuestionRepository = typeQuestionRepository;
    }

    @Override
    public Map<String, Object> search(SearchQuestionDTO searchQuestionDTO, Integer page, Integer pageSize) {
        List<QuestionDTO> lstQuestion = questionRepositoryCustomImpl.search(searchQuestionDTO, page, pageSize);
        Integer total = questionRepositoryCustomImpl.export(searchQuestionDTO).size();
        Map<String, Object> res = new HashMap<>();
        res.put("lstQuestion", lstQuestion);
        res.put("totalRecord", total);
        return res;
    }

    @Override
    public List<QuestionDTO> export(SearchQuestionDTO searchQuestionDTO) {
        return questionRepositoryCustomImpl.export(searchQuestionDTO);
    }

    @Override
    public QuestionDTO findById(Long id) {
        QuestionDTO questionDTO = questionRepositoryCustomImpl.findByQuestionId(id);
        // Đáp án câu hỏi
        List<Choice> lstChoice = choiceRepository.getListChoiceByQuestionId(questionDTO.getId());
        List<ChoiceDTO> lstDTO = new ArrayList<>();
        for (Choice choice : lstChoice) {
            ChoiceDTO choiceDTO = new ChoiceDTO();
            choiceDTO.setId(choice.getId());
            choiceDTO.setQuestionId(choice.getQuestionId());
            choiceDTO.setChoiceText(choice.getChoiceText());
            choiceDTO.setCorrected(choice.getCorrected());
            //            choiceDTO.setChoice(null);
            lstDTO.add(choiceDTO);
        }
        questionDTO.setLstChoice(lstDTO);
        return questionDTO;
    }

    @Override
    public byte[] exportFileTemplate() throws Exception {
        List<SheetConfigDTO> lstSheetConfigDTO = new ArrayList<>();
        List<QuestionDTO> lstQuestion = new ArrayList<>();
        lstSheetConfigDTO = getDataForExcel(SHEETNAME, lstQuestion, lstSheetConfigDTO, AppConstants.EXPORT_TEMPLATE);
        try {
            return fileExportUtil.exportXLSX(true, lstSheetConfigDTO, null);
        } catch (IOException ioE) {
            throw new Exception("Error When Export excel file in Resale sales double deposit total: " + ioE.getMessage(), ioE);
        }
    }

    @Override
    public ServiceResult<?> importQuestion(MultipartFile fileUploads, Long typeImport) throws Exception {
        return null;
    }

    @Override
    public byte[] exportExcelStudentErrors(List<QuestionDTO> listDataErrors) throws Exception {
        List<SheetConfigDTO> lstSheetConfigDTO = new ArrayList<>();
        lstSheetConfigDTO = getDataForExcel(SHEETNAME, listDataErrors, lstSheetConfigDTO, AppConstants.EXPORT_ERRORS);
        try {
            return fileExportUtil.exportXLSX(true, lstSheetConfigDTO, null);
        } catch (IOException ioE) {
            throw new Exception("Error When Export excel file in Resale sales double deposit total: " + ioE.getMessage(), ioE);
        }
    }

    private List<SheetConfigDTO> getDataForExcel(
        String sheetName,
        List<QuestionDTO> lstDataSheet,
        List<SheetConfigDTO> lstSheetConfigDTO,
        Long exportType
    ) {
        SheetConfigDTO sheetConfig = new SheetConfigDTO();
        String[] headerArr = null;
        if (exportType == 0L) {
            headerArr =
                new String[] {
                    "recordNo",
                    "question.level",
                    "question.content",
                    "question.choice1",
                    "question.choice2",
                    "question.choice3",
                    "question.choice4",
                    "question.choice.true",
                };
        } else {
            headerArr =
                new String[] {
                    "recordNo",
                    "question.level",
                    "question.content",
                    "question.choice1",
                    "question.choice2",
                    "question.choice3",
                    "question.choice4",
                    "question.choice.true",
                    "descriptionErrors",
                };
        }
        String[] lstChoice = { "1", "2", "3", "4" };
        String[] lstLevel = { "0", "1", "2" };
        sheetConfig.setSheetName(sheetName);
        sheetConfig.setHeaders(headerArr);
        int recordNo = 1;
        List<CellConfigDTO> cellConfigCustomList = new ArrayList<>();
        if (exportType != AppConstants.EXPORT_DATA) {
            cellConfigCustomList.add(new CellConfigDTO("choiceTrue", AppConstants.ALIGN_LEFT, lstChoice, 1, 99, 7, 7));
            cellConfigCustomList.add(new CellConfigDTO("level", AppConstants.ALIGN_LEFT, lstLevel, 1, 99, 1, 1));

            if (exportType != AppConstants.EXPORT_ERRORS) {
                for (int i = 1; i < 4; i++) {
                    QuestionDTO data = new QuestionDTO();
                    data.setRecordNo(i);
                    lstDataSheet.add(data);
                }
            }
            if (exportType == AppConstants.EXPORT_ERRORS) {
                for (QuestionDTO item : lstDataSheet) {
                    item.setRecordNo(recordNo++);
                    item.setMessageStr(String.join(AppConstants.NEXT_LINE, item.getMessageErr()));
                }
            }
        }

        sheetConfig.setList(lstDataSheet);
        List<CellConfigDTO> cellConfigList = new ArrayList<>();

        cellConfigList.add(new CellConfigDTO("recordNo", AppConstants.ALIGN_RIGHT, AppConstants.NO));
        cellConfigList.add(new CellConfigDTO("level", AppConstants.ALIGN_LEFT, AppConstants.STRING));
        cellConfigList.add(new CellConfigDTO("questionText", AppConstants.ALIGN_LEFT, AppConstants.STRING));
        cellConfigList.add(new CellConfigDTO("choice1", AppConstants.ALIGN_LEFT, AppConstants.STRING));
        cellConfigList.add(new CellConfigDTO("choice2", AppConstants.ALIGN_LEFT, AppConstants.STRING));
        cellConfigList.add(new CellConfigDTO("choice3", AppConstants.ALIGN_LEFT, AppConstants.STRING));
        cellConfigList.add(new CellConfigDTO("choice4", AppConstants.ALIGN_LEFT, AppConstants.STRING));
        cellConfigList.add(new CellConfigDTO("choiceTrue", AppConstants.ALIGN_LEFT, AppConstants.STRING));

        if (exportType == AppConstants.EXPORT_DATA || exportType == AppConstants.EXPORT_ERRORS) {
            cellConfigList.add(new CellConfigDTO("messageStr", AppConstants.ALIGN_LEFT, AppConstants.ERRORS));
        }

        sheetConfig.setHasIndex(false);
        sheetConfig.setHasBorder(true);
        sheetConfig.setExportType(exportType.intValue());
        sheetConfig.setCellConfigList(cellConfigList);
        sheetConfig.setCellCustomList(cellConfigCustomList);
        lstSheetConfigDTO.add(sheetConfig);
        return lstSheetConfigDTO;
    }
    //    private SubjectDTO processRecord(List<String> record, List<Classroom> lstClassroom, List<Subject> lstSubject, Long typeImport) {
    //        SubjectDTO subjectDTO = new SubjectDTO();
    //        List<String> messErr = new ArrayList<>();
    //        List<String> fieldErr = new ArrayList<>();
    //        Optional<User> userCreate = userService.getUserWithAuthorities();
    //
    //        int col = 1;
    //        if (typeImport == AppConstants.IMPORT_INSERT) {
    //            subjectDTO.setCreateName(userCreate.get().getLogin());
    //            subjectDTO.setCreateDate(Instant.now());
    //        } else {
    //            subjectDTO.setUpdateName(userCreate.get().getLogin());
    //            subjectDTO.setUpdateDate(Instant.now());
    //        }
    //        String subjectCode = record.get(col++);
    //        subjectDTO.setCode(subjectCode);
    //        Subject subject = lstSubject.stream().filter(c -> subjectCode.equalsIgnoreCase(c.getCode())).findAny().orElse(null);
    //        if (StringUtils.isBlank(subjectCode)) {
    //            messErr.add("Mã môn học không được để trống");
    //            fieldErr.add("code");
    //        } else if (subjectCode.length() < 0 || subjectCode.length() > 50) {
    //            messErr.add("Mã môn học không được quá 50 ký tự");
    //            fieldErr.add("code");
    //        } else if (typeImport == AppConstants.IMPORT_UPDATE) {
    //            if (null != subject) {
    //                subject.setId(subject.getId());
    //                subjectDTO.setCreateDate(subject.getCreateDate());
    //                subjectDTO.setCreateName(subject.getCreateName());
    //            }
    //        } else {
    //            if (null != subject) {
    //                messErr.add("Mã môn học đã tồn tại");
    //                fieldErr.add("code");
    //            }
    //        }
    //
    //        String subjectName = record.get(col++);
    //        subjectDTO.setName(subjectName);
    //        if (StringUtils.isBlank(subjectName)) {
    //            messErr.add("Tên môn học không được để trống");
    //            fieldErr.add("name");
    //        } else if (subjectName.length() < 0 || subjectName.length() > 250) {
    //            messErr.add("Tên môn học không được quá 250 ký tự");
    //            fieldErr.add("name");
    //        }
    //
    //        String classCode = record.get(col++);
    //        subjectDTO.setClassCode(classCode);
    //        if (StringUtils.isBlank(classCode)) {
    //            messErr.add("Mã lớp học không được để trống");
    //            fieldErr.add("classCode");
    //        } else if (classCode.length() < 0 || classCode.length() > 50) {
    //            messErr.add("Mã lớp học không được quá 50 ký tự");
    //            fieldErr.add("classCode");
    //        }
    //        subjectDTO.setStatus(true);
    //        subjectDTO.setMessageErr(messErr);
    //        subjectDTO.setFieldErr(fieldErr);
    //        return subjectDTO;
    //    }

}
