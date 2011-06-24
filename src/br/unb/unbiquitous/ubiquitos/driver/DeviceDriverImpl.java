package br.unb.unbiquitous.ubiquitos.driver;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;
import br.unb.unbiquitous.ubiquitos.json.JSONException;
import br.unb.unbiquitous.ubiquitos.json.dataType.JSONDevice;
import br.unb.unbiquitous.ubiquitos.network.model.NetworkDevice;
import br.unb.unbiquitous.ubiquitos.uos.UosDeviceManager;
import br.unb.unbiquitous.ubiquitos.uos.adaptability.DeviceDriver;
import br.unb.unbiquitous.ubiquitos.uos.context.UOSMessageContext;
import br.unb.unbiquitous.ubiquitos.uos.messageEngine.dataType.UpDevice;
import br.unb.unbiquitous.ubiquitos.uos.messageEngine.dataType.UpDriver;
import br.unb.unbiquitous.ubiquitos.uos.messageEngine.dataType.UpService;
import br.unb.unbiquitous.ubiquitos.uos.messageEngine.messages.ServiceCall;
import br.unb.unbiquitous.ubiquitos.uos.messageEngine.messages.ServiceResponse;

public class DeviceDriverImpl implements DeviceDriver {

	private static final String TAG = "DroidMouse - [DeviceDriverImpl]";

	private static final String DEVICE_KEY = "device";

	private static final String SECURITY_TYPE_KEY = "securityType";

	private static final String DRIVER_LIST_KEY = "driverList";

	private static final String DRIVER_NAME_KEY = "driverName";

	private static final String SERVICE_NAME_KEY = "serviceName";

	private UosDeviceManager deviceManager;

	private String instanceId;
	
	/**
	 * @see DeviceDriver#listDrivers(ServiceCall, ServiceResponse,
	 *      UOSMessageContext)
	 */
	public void listDrivers(ServiceCall serviceCall,
			ServiceResponse serviceResponse) {
		Log.d(TAG, "Handling listDrivers service");
	}
	
	@Override
	public void authenticate(ServiceCall serviceCall,
			ServiceResponse serviceResponse) {
		// TODO Auto-generated method stub
	}

	/**
	 * @see DeviceDriver#handshake(ServiceCall, ServiceResponse,
	 *      UOSMessageContext)
	 */
	public void handshake(ServiceCall serviceCall,
			ServiceResponse serviceResponse) {
		Log.d(TAG, "Handling handshake service");

		// Get and Convert the UpDevice Parameter
		String deviceParameter = serviceCall.getParameters().get(DEVICE_KEY);
		UpDevice device = null;
		if (deviceParameter != null) {
			try {
				device = new JSONDevice(deviceParameter).getAsObject();
			} catch (JSONException e) {
				serviceResponse.setError(e.getMessage());
			}
		}

		// TODO: [Fabs] : validate if the device doing the handshake is the same
		// that is in the parameter

		try {
			Map<String, String> response = new HashMap<String, String>();
			response.put(DEVICE_KEY, new JSONDevice(deviceManager.getCurrentDevice()).toString());
			serviceResponse.setResponseData(response);
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	@Override
	public UpDriver getDriver() {
		UpDriver driver = new UpDriver(
				"br.unb.unbiquitous.ubiquitos.driver.DeviceDriver");

		// populate services

		// populate listDrivers service
		driver.addService("listDrivers").addParameter(DRIVER_NAME_KEY,
				UpService.ParameterType.OPTIONAL).addParameter(
				SERVICE_NAME_KEY, UpService.ParameterType.OPTIONAL);

		// populate authenticate service
		driver.addService("authenticate").addParameter(SECURITY_TYPE_KEY,
				UpService.ParameterType.MANDATORY);

		// populate authenticate service
		driver.addService("handshake").addParameter(DEVICE_KEY,
				UpService.ParameterType.MANDATORY);

		// TODO : [Fabs] : Implements
		driver.addEvent("deviceEntered");
		driver.addEvent("deviceLeft");

		return driver;
	}

	public void init(UosDeviceManager deviceManager, String instanceId) {
		this.deviceManager = deviceManager;
		this.instanceId = instanceId;
	}

	@Override
	public void handleServiceCall(ServiceCall serviceCall,
			ServiceResponse serviceResponse, NetworkDevice networkDevice) {
		if (serviceCall.getService().equals("listDrivers")) {
			listDrivers(serviceCall, serviceResponse);
		} else if (serviceCall.getService().equals("handshake")) {
			handshake(serviceCall, serviceResponse);
		}
	}

	@Override
	public void tearDown() {
	}

}
