package ftp.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.builder.SpringApplicationBuilder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by kosta on 25.06.2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class ApplicationWebXmlTest {

    @Spy
    ApplicationWebXml applicationWebXml;

    @Test
    public void should_set_application_sources() {
        //given
        SpringApplicationBuilder app = mock(SpringApplicationBuilder.class);

        //when
        applicationWebXml.configure(app);

        //then
        verify(app).sources(BootLoader.class);
    }
}