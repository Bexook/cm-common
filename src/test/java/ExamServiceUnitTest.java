import com.cm.common.mapper.OrikaBeanMapper;
import com.cm.common.repository.ExamRepository;
import com.cm.common.service.course.CourseService;
import org.junit.BeforeClass;
import org.mockito.Mock;
import org.mockito.Mockito;

public class ExamServiceUnitTest {


    private static OrikaBeanMapper mapper;
    @Mock
    private static ExamRepository examRepository;
    @Mock
    private static CourseService courseService;


    @BeforeClass
    public static void beforeClass() {
        mapper = new OrikaBeanMapper();
        examRepository = Mockito.mock(ExamRepository.class);
        courseService = Mockito.mock(CourseService.class);
    }

    public void getExamDataForCourse_test_success() {

    }

    public void getExamDataForCourse_test_NO_EXAMINATION_COURSE_STATUSE() {

    }


    public void updateExam_test_success() {

    }


    public void updateExam_test_exam_does_not_exist() {

    }

}
