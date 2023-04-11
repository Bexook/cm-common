import com.cm.common.exception.SystemException;
import com.cm.common.mapper.OrikaBeanMapper;
import com.cm.common.model.dto.*;
import com.cm.common.model.enumeration.CourseProgressStatus;
import com.cm.common.model.enumeration.ExamStatus;
import com.cm.common.repository.ExamEvaluationRepository;
import com.cm.common.service.course.CourseService;
import com.cm.common.service.exam.ExamEvaluationService;
import com.cm.common.service.exam.ExamService;
import com.cm.common.service.exam.impl.ExamEvaluationServiceImpl;
import com.cm.common.service.homework.HomeworkService;
import com.cm.common.util.AuthorizationUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;
import utils.TestUtils;

import java.util.List;
import java.util.Set;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AuthorizationUtil.class})
public class ExamEvaluationServiceUnitTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static ExamEvaluationService examEvaluationService;
    @Mock
    public static CourseService courseService;
    private static OrikaBeanMapper mapper;

    @Mock
    private static HomeworkService homeworkService;
    @Mock
    private static ExamEvaluationRepository examEvaluationRepository;
    @Mock
    private static ExamService examService;


    @BeforeClass
    public static void beforeClass() {
        mapper = Mockito.mock(OrikaBeanMapper.class);
        examService = Mockito.mock(ExamService.class);
        courseService = Mockito.mock(CourseService.class);
        homeworkService = Mockito.mock(HomeworkService.class);
        examEvaluationRepository = Mockito.mock(ExamEvaluationRepository.class);
        examEvaluationService = new ExamEvaluationServiceImpl(homeworkService, examEvaluationRepository, mapper, examService, courseService);
        ReflectionTestUtils.setField(examEvaluationService, "percentsOfPointsToPassEvaluation", 80);
    }

    @Test
    public void evaluateExamGradeForCourse_evaluated_certified() {
        TestUtils.mockAuthorizationUtil();
        final CourseDTO courseDTO = new CourseDTO().setAmountOfPoints(30);
        final AnswerDTO testAnswer = new AnswerDTO().setAnswerValue("TEST ASNWER").setRightAnswer(true);
        final List<AnswerDTO> answers = List.of(
                new AnswerDTO().setAnswerValue("TEST ASNWER").setRightAnswer(true),
                new AnswerDTO().setAnswerValue("TEST ASNWER").setRightAnswer(false)
        );
        final List<QuestionDTO> questions = List.of(
                new QuestionDTO().setQuestionText("TEST QUEST").setAmountOfPoints(10).setAnswers(answers),
                new QuestionDTO().setQuestionText("TEST QUEST").setAmountOfPoints(10).setAnswers(answers)
        );
        final ExamDTO examDTO = new ExamDTO();
        examDTO.setCourse(courseDTO);
        examDTO.setMinGrade(10);
        examDTO.setQuestions(questions);
        examDTO.setMaxGrade(20);
        final ExamEvaluationDTO exam = new ExamEvaluationDTO();
        exam.setExamStatus(ExamStatus.FINISHED);
        exam.setExam(examDTO);
        exam.setUserResults(Set.of(new QuestionResultDTO().setAmountOfPoints(10).setUserAnswer(testAnswer)));
        Mockito.when(homeworkService.evaluateHomeworkUserGradeForCourse(Mockito.any(), Mockito.any())).thenReturn(20);
        Mockito.when(mapper.map(examEvaluationRepository.findById(Mockito.any()), ExamEvaluationDTO.class)).thenReturn(exam);
        final UserEvaluationResultDTO userEvaluationResultDTO = examEvaluationService.evaluateExamGradeForCourse(1L);
        Assert.assertEquals(CourseProgressStatus.CERTIFIED, userEvaluationResultDTO.getCourseProgressStatus());
    }


    @Test
    public void evaluateExamGradeForCourse_evaluated_failed() {
        TestUtils.mockAuthorizationUtil();
        final CourseDTO courseDTO = new CourseDTO().setAmountOfPoints(30);
        final AnswerDTO testAnswer = new AnswerDTO().setAnswerValue("TEST ASNWER").setRightAnswer(false);
        final List<AnswerDTO> answers = List.of(
                new AnswerDTO().setAnswerValue("TEST ASNWER").setRightAnswer(true),
                new AnswerDTO().setAnswerValue("TEST ASNWER").setRightAnswer(false)
        );
        final List<QuestionDTO> questions = List.of(
                new QuestionDTO().setQuestionText("TEST QUEST").setAmountOfPoints(10).setAnswers(answers),
                new QuestionDTO().setQuestionText("TEST QUEST").setAmountOfPoints(10).setAnswers(answers)
        );
        final ExamDTO examDTO = new ExamDTO();
        examDTO.setCourse(courseDTO);
        examDTO.setMinGrade(10);
        examDTO.setQuestions(questions);
        examDTO.setMaxGrade(20);
        final ExamEvaluationDTO exam = new ExamEvaluationDTO();
        exam.setExamStatus(ExamStatus.FINISHED);
        exam.setExam(examDTO);
        exam.setUserResults(Set.of(new QuestionResultDTO().setAmountOfPoints(0).setUserAnswer(testAnswer)));
        Mockito.when(homeworkService.evaluateHomeworkUserGradeForCourse(Mockito.any(), Mockito.any())).thenReturn(20);
        Mockito.when(mapper.map(examEvaluationRepository.findById(Mockito.any()), ExamEvaluationDTO.class)).thenReturn(exam);
        final UserEvaluationResultDTO userEvaluationResultDTO = examEvaluationService.evaluateExamGradeForCourse(1L);
        Assert.assertEquals(CourseProgressStatus.FAILED, userEvaluationResultDTO.getCourseProgressStatus());
    }


    @Test
    public void evaluateExamGradeForCourse_exam_not_finished() {
        TestUtils.mockAuthorizationUtil();
        thrown.expect(SystemException.class);
        thrown.expectMessage("Finish exam before evaluating results");
        final CourseDTO courseDTO = new CourseDTO().setAmountOfPoints(30);
        final AnswerDTO testAnswer = new AnswerDTO().setAnswerValue("TEST ASNWER").setRightAnswer(false);
        final List<AnswerDTO> answers = List.of(
                new AnswerDTO().setAnswerValue("TEST ASNWER").setRightAnswer(true),
                new AnswerDTO().setAnswerValue("TEST ASNWER").setRightAnswer(false)
        );
        final List<QuestionDTO> questions = List.of(
                new QuestionDTO().setQuestionText("TEST QUEST").setAmountOfPoints(10).setAnswers(answers),
                new QuestionDTO().setQuestionText("TEST QUEST").setAmountOfPoints(10).setAnswers(answers)
        );
        final ExamDTO examDTO = new ExamDTO();
        examDTO.setCourse(courseDTO);
        examDTO.setMinGrade(10);
        examDTO.setQuestions(questions);
        examDTO.setMaxGrade(20);
        final ExamEvaluationDTO exam = new ExamEvaluationDTO();
        exam.setExamStatus(ExamStatus.DRAFT);
        exam.setExam(examDTO);
        exam.setUserResults(Set.of(new QuestionResultDTO().setAmountOfPoints(0).setUserAnswer(testAnswer)));
        Mockito.when(homeworkService.evaluateHomeworkUserGradeForCourse(Mockito.any(), Mockito.any())).thenReturn(20);
        Mockito.when(mapper.map(examEvaluationRepository.findById(Mockito.any()), ExamEvaluationDTO.class)).thenReturn(exam);
        examEvaluationService.evaluateExamGradeForCourse(1L);
    }
}
