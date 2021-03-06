package org.nzbhydra.indexers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.nzbhydra.config.BaseConfig;
import org.nzbhydra.config.ConfigProvider;
import org.nzbhydra.config.SearchingConfig;
import org.nzbhydra.config.indexer.IndexerConfig;
import org.nzbhydra.mapping.newznab.xml.NewznabXmlRoot;
import org.nzbhydra.okhttp.WebAccess;
import org.springframework.oxm.Unmarshaller;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class IndexerWebAccessTest {

    @Mock
    private SearchingConfig searchingConfigMock;
    @Mock
    private ConfigProvider configProviderMock;
    @Mock
    private WebAccess webAccessMock;
    private IndexerConfig indexerConfig = new IndexerConfig();
    @Mock
    private Unmarshaller unmarshallerMock;
    @Captor
    ArgumentCaptor<Map<String, String>> headersCaptor;
    @Captor
    ArgumentCaptor<Integer> timeoutCaptor;

    @InjectMocks
    private IndexerWebAccess testee = new IndexerWebAccess();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        String xml = "<?xml version=\"1.0\" ?>\n" +
                "<metadata>\n" +
                "</metadata>";
        when(webAccessMock.callUrl(anyString(), headersCaptor.capture(), timeoutCaptor.capture())).thenReturn(xml);
        BaseConfig baseConfig = new BaseConfig();
        baseConfig.setSearching(searchingConfigMock);
        when(configProviderMock.getBaseConfig()).thenReturn(baseConfig);
        when(searchingConfigMock.getUserAgent()).thenReturn(Optional.of("globalUa"));
        when(searchingConfigMock.getTimeout()).thenReturn(100);
        indexerConfig.setTimeout(10);
        indexerConfig.setUserAgent("indexerUa");
        when(unmarshallerMock.unmarshal(any())).thenReturn(new NewznabXmlRoot());
    }

    @Test
    public void shouldUseIndexerUserAgent() throws Exception{
        testee.get(new URI("http://127.0.0.1"), indexerConfig);
        Map<String, String> headers = headersCaptor.getValue();
        assertThat(headers).contains(entry("User-Agent", "indexerUa"));
    }

    @Test
    public void shouldUseGlobalUserAgentIfNoIndexerUaIsSet() throws Exception{
        indexerConfig.setUserAgent(null);

        testee.get(new URI("http://127.0.0.1"), indexerConfig);

        Map<String, String> headers = headersCaptor.getValue();
        assertThat(headers).contains(entry("User-Agent", "globalUa"));
    }

    @Test
    public void shouldUseIndexerTimeout() throws Exception{
        testee.get(new URI("http://127.0.0.1"), indexerConfig);
        assertThat(timeoutCaptor.getValue()).isEqualTo(10);
    }

    @Test
    public void shouldUseGlobalTimeoutNoIndexerTimeoutIsSet() throws Exception{
        indexerConfig.setTimeout(null);

        testee.get(new URI("http://127.0.0.1"), indexerConfig);

        assertThat(timeoutCaptor.getValue()).isEqualTo(100);
    }




}