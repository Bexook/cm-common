package utils;

import com.cm.common.model.domain.AppUserEntity;
import com.cm.common.security.AppUserDetails;
import com.cm.common.util.AuthorizationUtil;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

public class TestUtils {

    public static void mockAuthorizationUtil() {
        mockStatic(AuthorizationUtil.class);
        final AppUserEntity mockedEntity = new AppUserEntity();
        mockedEntity.setId(1L);
        final AppUserDetails mockedUserDetails = new AppUserDetails(mockedEntity);
        when(AuthorizationUtil.getCurrentUser()).thenReturn(mockedUserDetails);
    }
}
