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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
@ContextConfiguration(locations = { "classpath*:META-INF/spring/TEST-fdh-rabbitmq-handler-scan-context.xml",
"classpath:/META-INF/spring/ext-util-scan-context.xml" })
@ActiveProfiles({ "rabbitmq" })
public class RabbitMQHandlerPutFieldDataTest {

	private static final Logger log = LoggerFactory.getLogger(RabbitMQHandlerPutFieldDataTest.class);
	
	@Autowired
	TestData testData;

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
	@Autowired
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
	 * This is the best practice, use a DataMap
	 * @throws IOException
	 *             -
	 * @throws IllegalStateException
	 *             -
	 */
	@SuppressWarnings("nls")
	@Test
	public void testPutFieldDataUsingDataMap() throws IllegalStateException, IOException {
		log.debug("================================");
		ConnectionFactory mockConnectionFactory = mock(ConnectionFactory.class);
		Connection mockConnection = mock(Connection.class);
		Channel mockChannel = mock(Channel.class);

		when(mockConnectionFactory.newConnection((ExecutorService) null)).thenReturn(mockConnection);
		when(mockConnection.isOpen()).thenReturn(true);
		when(mockConnection.createChannel()).thenReturn(mockChannel);

		when(mockChannel.isOpen()).thenReturn(true);

		PutFieldDataRequest request = this.testData.putFieldDataRequestUsingDataMap();
		List<Header> headers = new ArrayList<Header>();
		Map<Integer, Object> modelLookupMap = new HashMap<Integer, Object>();
		PutFieldDataResult result = this.rabbitMqHandler.putData(request, modelLookupMap, headers, null);
		Assert.assertNotNull(result);
	}
	
	/**
	 * This is the best practice, use a DataMap
	 * @throws IOException
	 *             -
	 * @throws IllegalStateException
	 *             -
	 */
	@SuppressWarnings("nls")
	@Test
	public void testPutFieldDataUsingDataMapList() throws IllegalStateException, IOException {
		log.debug("================================");
		ConnectionFactory mockConnectionFactory = mock(ConnectionFactory.class);
		Connection mockConnection = mock(Connection.class);
		Channel mockChannel = mock(Channel.class);

		when(mockConnectionFactory.newConnection((ExecutorService) null)).thenReturn(mockConnection);
		when(mockConnection.isOpen()).thenReturn(true);
		when(mockConnection.createChannel()).thenReturn(mockChannel);

		when(mockChannel.isOpen()).thenReturn(true);

		PutFieldDataRequest request = this.testData.putFieldDataRequestUsingDataMapList();
		List<Header> headers = new ArrayList<Header>();
		Map<Integer, Object> modelLookupMap = new HashMap<Integer, Object>();
		PutFieldDataResult result = this.rabbitMqHandler.putData(request, modelLookupMap, headers, null);
		Assert.assertNotNull(result);
	}
	
	/**
	 * PredixString is NOT the best practice, use a DataMap instead
	 * @throws IOException
	 *             -
	 * @throws IllegalStateException
	 *             -
	 */
	@SuppressWarnings("nls")
	@Test
	public void testPutFieldDataUsingPredixString() throws IllegalStateException, IOException {
		log.debug("================================");
		ConnectionFactory mockConnectionFactory = mock(ConnectionFactory.class);
		Connection mockConnection = mock(Connection.class);
		Channel mockChannel = mock(Channel.class);

		when(mockConnectionFactory.newConnection((ExecutorService) null)).thenReturn(mockConnection);
		when(mockConnection.isOpen()).thenReturn(true);
		when(mockConnection.createChannel()).thenReturn(mockChannel);

		when(mockChannel.isOpen()).thenReturn(true);

		PutFieldDataRequest request = TestData.putFieldDataRequestUsingPredixString(null);
		List<Header> headers = new ArrayList<Header>();
		Map<Integer, Object> modelLookupMap = new HashMap<Integer, Object>();
		PutFieldDataResult result = this.rabbitMqHandler.putData(request, modelLookupMap, headers, null);
		Assert.assertNotNull(result);
	}

}
