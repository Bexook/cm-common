import com.cm.common.exception.SystemException;
import com.cm.common.mapper.OrikaBeanMapper;
import com.cm.common.model.domain.CourseEntity;
import com.cm.common.model.dto.AppUserDTO;
import com.cm.common.model.enumeration.CourseAuthorities;
import com.cm.common.model.enumeration.CourseProgressStatus;
import com.cm.common.model.enumeration.UserRole;
import com.cm.common.repository.CourseRepository;
import com.cm.common.service.course.CourseService;
import com.cm.common.service.course.impl.CourseServiceImpl;
import com.cm.common.service.exam.ExamService;
import com.cm.common.service.lesson.LessonService;
import com.cm.common.service.user.AppUserService;
import com.cm.common.util.AuthorizationUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.Optional;

import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AuthorizationUtil.class})
public class CourseServiceUnitTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    public static CourseService courseService;
    private static OrikaBeanMapper mapper;
    @Mock
    private static CourseRepository courseRepository;
    @Mock
    private static AppUserService appUserService;
    @Mock
    private static ExamService examService;
    @Mock
    private static LessonService lessonService;


    @BeforeClass
    public static void beforeClass() {

        mapper = new OrikaBeanMapper();
        courseRepository = Mockito.mock(CourseRepository.class);
        appUserService = Mockito.mock(AppUserService.class);
        examService = Mockito.mock(ExamService.class);
        lessonService = Mockito.mock(LessonService.class);
        courseService = new CourseServiceImpl(mapper, courseRepository, appUserService, examService, lessonService);
        ReflectionTestUtils.setField(courseService, "userAvailableAmount", 5);
    }


    @Test
    public void getUserCourseProgressStatus_test_success() {
        TestUtils.mockAuthorizationUtil();
        Mockito.when(courseRepository.getCourseProgressStatus(1L, 2L)).thenReturn(CourseProgressStatus.IN_PROGRESS);
        final CourseProgressStatus result = courseService.getUserCourseProgressStatus(2L);
        Assert.assertEquals(result, CourseProgressStatus.IN_PROGRESS);
    }


    @Test
    public void updateCourseAvailabilityStatus_test_success() {
        final CourseEntity courseMocked = new CourseEntity()
                .setAvailable(true)
                .setAmountOfPoints(60);
        Mockito.when(courseRepository.findById(2L)).thenReturn(Optional.of(courseMocked));
        courseService.updateCourseAvailabilityStatus(2L, false);
    }

    @Test
    public void updateCourseAvailabilityStatus_test_failed_please_specify_amount_of_points_bigger_than_50() {
        thrown.expect(SystemException.class);
        thrown.expectMessage("Please specify amount of points bigger or equal to 50");
        final CourseEntity courseMocked = new CourseEntity()
                .setAvailable(true);
        Mockito.when(courseRepository.findById(2L)).thenReturn(Optional.of(courseMocked));
        courseService.updateCourseAvailabilityStatus(2L, false);
    }

    @Test
    public void updateCourseAvailabilityStatus_test_failed_course_does_not_exist() {
        thrown.expect(SystemException.class);
        thrown.expectMessage("Course does not exist");
        Mockito.when(courseRepository.findById(2L)).thenReturn(Optional.empty());
        courseService.updateCourseAvailabilityStatus(2L, false);
    }

    @Test
    public void getCourseOverviewById_test_success() {
        final CourseEntity mockedCourse = new CourseEntity();
        mockedCourse.setId(2L);
        mockedCourse.setAvailable(true);
        mockedCourse.setDescription("Test description");
        when(courseRepository.findOne(Mockito.any())).thenReturn(Optional.of(mockedCourse));
        courseService.getCourseOverviewById(2L);
    }

    @Test
    public void addTeacherToCourseWithAuthorities_test_success() {
        when(appUserService.getUserById(Mockito.any())).thenReturn(new AppUserDTO().setId(1L).setUserRole(UserRole.TEACHER));
        courseService.addTeacherToCourseWithAuthorities(2L, 2L, List.of(CourseAuthorities.UPDATE_COURSE));
    }

    @Test
    public void addTeacherToCourseWithAuthorities_test_user_does_not_have_need_role_student() {
        thrown.expect(SystemException.class);
        thrown.expectMessage("User does not contain required role");
        when(appUserService.getUserById(Mockito.any())).thenReturn(new AppUserDTO().setId(1L).setUserRole(UserRole.STUDENT));
        courseService.addTeacherToCourseWithAuthorities(2L, 2L, List.of(CourseAuthorities.UPDATE_COURSE));
    }

    @Test
    public void addTeacherToCourseWithAuthorities_test_user_does_not_have_need_role_admin() {
        thrown.expect(SystemException.class);
        thrown.expectMessage("User does not contain required role");
        when(appUserService.getUserById(Mockito.any())).thenReturn(new AppUserDTO().setId(1L).setUserRole(UserRole.ADMIN));
        courseService.addTeacherToCourseWithAuthorities(2L, 2L, List.of(CourseAuthorities.UPDATE_COURSE));
    }

    @Test
    public void updateCourseAuthoritiesForTeacherById_test_success() throws JsonProcessingException {
        when(courseRepository.isUserAlreadyRegisteredToCourse(Mockito.any(), Mockito.any())).thenReturn(true);
        when(appUserService.getUserById(Mockito.any())).thenReturn(new AppUserDTO().setId(1L).setUserRole(UserRole.TEACHER));
        courseService.updateCourseAuthoritiesForTeacherById(2L, 2L, List.of(CourseAuthorities.UPDATE_COURSE));
    }


    @Test
    public void updateCourseAuthoritiesForTeacherById_test_user_does_not_have_need_role_admin() throws JsonProcessingException {
        thrown.expect(SystemException.class);
        thrown.expectMessage("User does not contain required role");
        when(courseRepository.isUserAlreadyRegisteredToCourse(Mockito.any(), Mockito.any())).thenReturn(true);
        when(appUserService.getUserById(Mockito.any())).thenReturn(new AppUserDTO().setId(1L).setUserRole(UserRole.ADMIN));
        courseService.updateCourseAuthoritiesForTeacherById(2L, 2L, List.of(CourseAuthorities.UPDATE_COURSE));
    }

    @Test
    public void updateCourseAuthoritiesForTeacherById_test_user_does_not_have_need_role_student() throws JsonProcessingException {
        thrown.expect(SystemException.class);
        thrown.expectMessage("User does not contain required role");
        when(courseRepository.isUserAlreadyRegisteredToCourse(Mockito.any(), Mockito.any())).thenReturn(true);
        when(appUserService.getUserById(Mockito.any())).thenReturn(new AppUserDTO().setId(1L).setUserRole(UserRole.STUDENT));
        courseService.updateCourseAuthoritiesForTeacherById(2L, 2L, List.of(CourseAuthorities.UPDATE_COURSE));
    }


    @Test
    public void updateCourseStatusForUserByCourseIdAndUserId_test_success() {
        when(courseRepository.existsById(Mockito.any())).thenReturn(true);
        courseService.updateCourseStatusForUserByCourseIdAndUserId(2L, 2L, CourseProgressStatus.CERTIFIED);
    }

    @Test
    public void updateCourseStatusForUserByCourseIdAndUserId_test_failed_course_does_not_exist() {
        thrown.expect(SystemException.class);
        thrown.expectMessage("Course does not exist");
        when(courseRepository.existsById(Mockito.any())).thenReturn(false);
        courseService.updateCourseStatusForUserByCourseIdAndUserId(2L, 2L, CourseProgressStatus.CERTIFIED);
    }

    @Test
    public void registerStudentUserToCourse_test_success() {
        TestUtils.mockAuthorizationUtil();
        when(courseRepository.exists(Mockito.any())).thenReturn(true);
        when(courseRepository.countUserCoursesByStatus(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(2);
        when(courseRepository.isUserAlreadyRegisteredToCourse(Mockito.any(), Mockito.any())).thenReturn(false);
        courseService.registerStudentUserToCourse(2L);
    }

    @Test
    public void registerStudentUserToCourse_test_user_already_registered() {
        TestUtils.mockAuthorizationUtil();
        thrown.expect(SystemException.class);
        thrown.expectMessage("User already registered");
        when(courseRepository.exists(Mockito.any())).thenReturn(true);
        when(courseRepository.countUserCoursesByStatus(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(2);
        when(courseRepository.isUserAlreadyRegisteredToCourse(Mockito.any(), Mockito.any())).thenReturn(true);
        courseService.registerStudentUserToCourse(2L);

    }

    @Test
    public void registerStudentUserToCourse_test_user_already_registered_to_5_courses() {
        TestUtils.mockAuthorizationUtil();
        when(courseRepository.exists(Mockito.any())).thenReturn(true);
        when(courseRepository.countUserCoursesByStatus(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(5);
        when(courseRepository.isUserAlreadyRegisteredToCourse(Mockito.any(), Mockito.any())).thenReturn(false);
        thrown.expect(SystemException.class);
        thrown.expectMessage("Acceded available amount of courses");
        courseService.registerStudentUserToCourse(2L);
    }

    @Test
    public void registerStudentUserToCourse_test_course_does_not_exist() {
        TestUtils.mockAuthorizationUtil();
        when(courseRepository.exists(Mockito.any())).thenReturn(false);
        when(courseRepository.countUserCoursesByStatus(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(2);
        when(courseRepository.isUserAlreadyRegisteredToCourse(Mockito.any(), Mockito.any())).thenReturn(false);
        thrown.expect(SystemException.class);
        thrown.expectMessage("Course does not exist");
        courseService.registerStudentUserToCourse(2L);
    }


}
