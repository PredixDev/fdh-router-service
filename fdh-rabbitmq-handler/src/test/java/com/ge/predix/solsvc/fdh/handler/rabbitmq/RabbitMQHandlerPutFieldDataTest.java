package com.ge.predix.solsvc.fdh.handler.rabbitmq;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.apache.http.Header;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ge.predix.entity.model.Model;
import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.putfielddata.PutFieldDataResult;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 
 * @author predix
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/TEST-fdh-rabbitmq-handler-scan-context.xml" })
@ActiveProfiles({ "rabbitmq" })
public class RabbitMQHandlerPutFieldDataTest {

	private static final Logger log = LoggerFactory.getLogger(RabbitMQHandlerPutFieldDataTest.class);

	/**
	 * @throws Exception
	 *             -
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//
	}

	/**
	 * @throws Exception
	 *             -
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		//
	}

	@InjectMocks
	private RabbitMQHandler rabbitMqHandler;

	@Mock
	private MessageConverter messageConverter;

	@Mock
	private RabbitTemplate eventTemplate;

	/**
	 * @throws Exception
	 *             -
	 */
	@SuppressWarnings({})
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.rabbitMqHandler.setMessageConverter(this.messageConverter);
		this.rabbitMqHandler.setEventTemplate(this.eventTemplate);
	}

	/**
	 * @throws Exception
	 *             -
	 */
	@After
	public void tearDown() throws Exception {
		//
	}

	/**
	 * @throws IOException
	 *             -
	 * @throws IllegalStateException
	 *             -
	 */
	@SuppressWarnings("nls")
	@Test
	public void testPutFieldData() throws IllegalStateException, IOException {
		log.debug("================================");
		ConnectionFactory mockConnectionFactory = mock(ConnectionFactory.class);
		Connection mockConnection = mock(Connection.class);
		Channel mockChannel = mock(Channel.class);

		when(mockConnectionFactory.newConnection((ExecutorService) null)).thenReturn(mockConnection);
		when(mockConnection.isOpen()).thenReturn(true);
		when(mockConnection.createChannel()).thenReturn(mockChannel);

		when(mockChannel.isOpen()).thenReturn(true);

		PutFieldDataRequest request = TestData.putFieldDataRequest();
		List<Header> headers = new ArrayList<Header>();
		Map<Integer, Model> modelLookupMap = new HashMap<Integer, Model>();
		PutFieldDataResult result = this.rabbitMqHandler.putData(request, modelLookupMap, headers, null);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.getErrorEvent().get(0).contains("SampleHandler"));

	}

}
