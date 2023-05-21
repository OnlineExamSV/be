package com.toeic.online.service.impl;

import com.toeic.online.commons.ExportUtils;
import com.toeic.online.commons.FileExportUtil;
import com.toeic.online.commons.FileUploadUtil;
import com.toeic.online.commons.Translator;
import com.toeic.online.constant.AppConstants;
import com.toeic.online.domain.*;
import com.toeic.online.repository.ChoiceRepository;
import com.toeic.online.repository.QuestionRepository;
import com.toeic.online.repository.SubjectRepository;
import com.toeic.online.repository.TypeQuestionRepository;
import com.toeic.online.repository.impl.QuestionRepositoryCustomImpl;
import com.toeic.online.service.QuestionService;
import com.toeic.online.service.UserService;
import com.toeic.online.service.dto.*;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
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

    private final SubjectRepository subjectRepository;

    List<ExcelDynamicDTO> lstError;

    private static final String SHEETNAME = "question.sheetName";

    public QuestionServiceImpl(
        QuestionRepositoryCustomImpl questionRepositoryCustomImpl,
        ChoiceRepository choiceRepository,
        QuestionRepository questionRepository,
        ExportUtils exportUtils,
        FileExportUtil fileExportUtil,
        UserService userService,
        TypeQuestionRepository typeQuestionRepository,
        SubjectRepository subjectRepository) {
        this.questionRepositoryCustomImpl = questionRepositoryCustomImpl;
        this.choiceRepository = choiceRepository;
        this.questionRepository = questionRepository;
        this.exportUtils = exportUtils;
        this.fileExportUtil = fileExportUtil;
        this.userService = userService;
        this.typeQuestionRepository = typeQuestionRepository;
        this.subjectRepository = subjectRepository;
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
    public byte[] exportExcelQuestionErrors(List<QuestionDTO> listDataErrors) throws Exception {
        List<SheetConfigDTO> lstSheetConfigDTO = new ArrayList<>();
        lstSheetConfigDTO = getDataForExcel(SHEETNAME, listDataErrors, lstSheetConfigDTO, AppConstants.EXPORT_ERRORS);
        try {
            return fileExportUtil.exportXLSX(true, lstSheetConfigDTO, null);
        } catch (IOException ioE) {
            throw new Exception("Error When Export excel file in Resale sales double deposit total: " + ioE.getMessage(), ioE);
        }
    }

    @Override
    public ServiceResult<?> importData(MultipartFile fileUploads) throws Exception {
        ResponseImportDTO importDTO = new ResponseImportDTO();
        if (!FileUploadUtil.isNotNullOrEmpty(fileUploads)) return new ServiceResult<>(null, HttpStatus.BAD_REQUEST, "File không tồn tại");
        String extention = FilenameUtils.getExtension(fileUploads.getOriginalFilename());
        if (
            !AppConstants.EXTENSION_XLSX.equals(extention.toLowerCase()) && !AppConstants.EXTENSION_XLS.equals(extention.toLowerCase())
        ) return new ServiceResult<>(null, HttpStatus.BAD_REQUEST, Translator.toLocale("msg.file.format"));

        List<List<String>> records;
        try {
            records = FileUploadUtil.excelReader(fileUploads);
        } catch (IllegalStateException | IOException e) {
            return new ServiceResult<>(null, HttpStatus.BAD_REQUEST, Translator.toLocale("msg.file.read"));
        }

        if (records.size() <= 1) return new ServiceResult<>(null, HttpStatus.BAD_REQUEST, Translator.toLocale("msg.file.no.data"));

        //remove record header
        records.remove(0);

        List<Question> dataSuccess = new ArrayList<>();
        List<QuestionDTO> dataErrors = new ArrayList<>();
        List<Question> lstQuestion = questionRepository.findAll();
        int countSuccess = 0;
        int countError = 0;
        int total = 0;
        for (List<String> record : records) {
            if (record.size() != 9) {
                return new ServiceResult<>(null, HttpStatus.BAD_REQUEST, Translator.toLocale("msg.file.errorTemplate"));
            }

            QuestionDTO dto = processRecord(record, 0L);
            if (dto.getMessageErr().size() > 0) {
                countError++;
                dataErrors.add(dto);
            } else {
                Question question = new Question(dto);
                dataSuccess.add(question);
                question = questionRepository.save(question);
                // Thêm câu trả lời
                Choice choice1 = new Choice();
                choice1.setQuestionId(question.getId());
                choice1.setChoiceText(dto.getChoice1());
                choice1.setCorrected(dto.getChoiceTrue().equals("1") ? "true": "false");
                choiceRepository.save(choice1);
                Choice choice2 = new Choice();
                choice2.setQuestionId(question.getId());
                choice2.setChoiceText(dto.getChoice2());
                choice2.setCorrected(dto.getChoiceTrue().equals("2") ? "true": "false");
                choiceRepository.save(choice2);
                Choice choice3 = new Choice();
                choice3.setQuestionId(question.getId());
                choice3.setChoiceText(dto.getChoice3());
                choice3.setCorrected(dto.getChoiceTrue().equals("3") ? "true": "false");
                choiceRepository.save(choice3);
                Choice choice4 = new Choice();
                choice4.setQuestionId(question.getId());
                choice4.setChoiceText(dto.getChoice4());
                choice4.setCorrected(dto.getChoiceTrue().equals("4") ? "true": "false");
                choiceRepository.save(choice4);
                lstQuestion.add(question);
                countSuccess++;
            }
            total++;
        }

        ResponseImportDTO responseImportDTO = new ResponseImportDTO(countError, countSuccess, total);
        responseImportDTO.setListErrors(dataErrors);
        responseImportDTO.setListSuccess(dataSuccess);
        return new ServiceResult<>(responseImportDTO, HttpStatus.OK, Translator.toLocale("msg.import.success"));
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
                    "question.subjectCode",
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
                    "question.subjectCode",
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
        List<Subject> lstSubject = subjectRepository.findAllByStatusActive();
        sheetConfig.setSheetName(sheetName);
        sheetConfig.setHeaders(headerArr);
        int recordNo = 1;
        List<String> lstSubjectCode = lstSubject.stream().map(Subject :: getCode).collect(Collectors.toList());
        List<CellConfigDTO> cellConfigCustomList = new ArrayList<>();
        if (exportType != AppConstants.EXPORT_DATA) {
            cellConfigCustomList.add(new CellConfigDTO("choiceTrue", AppConstants.ALIGN_LEFT, lstChoice, 1, 99, 8, 8));
            cellConfigCustomList.add(new CellConfigDTO("level", AppConstants.ALIGN_LEFT, lstLevel, 1, 99, 2, 2));
            cellConfigCustomList.add(new CellConfigDTO("subjectCode", AppConstants.ALIGN_LEFT, lstSubjectCode.toArray(new String[0]), 1, 99, 1, 1));

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
        cellConfigList.add(new CellConfigDTO("subjectCode", AppConstants.ALIGN_LEFT, AppConstants.STRING));
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
    private QuestionDTO processRecord(List<String> record, Long typeImport) {
            QuestionDTO questionDTO = new QuestionDTO();
            List<String> messErr = new ArrayList<>();
            List<String> fieldErr = new ArrayList<>();
            Optional<User> userCreate = userService.getUserWithAuthorities();

            int col = 1;
            if (typeImport == AppConstants.IMPORT_INSERT) {
                questionDTO.setCreateName(userCreate.get().getLogin());
                questionDTO.setCreateDate(Instant.now());
            } else {
                questionDTO.setUpdateName(userCreate.get().getLogin());
                questionDTO.setUpdateDate(Instant.now());
            }

            String subjectCode = record.get(col++);
            questionDTO.setSubjectCode(subjectCode);
            if (StringUtils.isBlank(subjectCode)) {
                messErr.add("Môn học không được để trống");
                fieldErr.add("subjectCode");
            }

            String level = record.get(col++);
            if (StringUtils.isBlank(level)) {
                messErr.add("Độ khó không được để trống");
                fieldErr.add("level");
            }else{
                questionDTO.setLevel(Integer.parseInt(level));
            }

            String questionText = record.get(col++);
            questionDTO.setQuestionText(questionText);
            if (StringUtils.isBlank(questionText)) {
                messErr.add("Nội dunng câu hỏi không được để trống");
                fieldErr.add("questionText");
            }

            String choice1 = record.get(col++);
            questionDTO.setChoice1(choice1);
            if (StringUtils.isBlank(choice1)) {
                messErr.add("Đáp án 1 không được để trống");
                fieldErr.add("choice1");
            }

            String choice2 = record.get(col++);
            questionDTO.setChoice2(choice2);
            if (StringUtils.isBlank(choice2)) {
                messErr.add("Đáp án 2 không được để trống");
                fieldErr.add("choice2");
            }

            String choice3 = record.get(col++);
            questionDTO.setChoice3(choice3);
            if (StringUtils.isBlank(choice3)) {
                messErr.add("Đáp án 3 không được để trống");
                fieldErr.add("choice3");
            }

            String choice4 = record.get(col++);
            questionDTO.setChoice4(choice4);
            if (StringUtils.isBlank(choice4)) {
                messErr.add("Đáp án 4 không được để trống");
                fieldErr.add("choice4");
            }

        String choiceTrue = record.get(col++);
        questionDTO.setChoiceTrue(choiceTrue);
        if (StringUtils.isBlank(choiceTrue)) {
            messErr.add("Đáp án đúng không được để trống");
            fieldErr.add("choiceTrue");
        }

            questionDTO.setStatus(true);
            questionDTO.setQuestionType("TN");
            questionDTO.setPoint(Float.parseFloat("10"));
            questionDTO.setMessageErr(messErr);
            questionDTO.setFieldErr(fieldErr);
            return questionDTO;
        }

}
