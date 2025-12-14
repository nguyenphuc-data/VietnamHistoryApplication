package com.example.vietnamhistoryapplication.utils;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

public class AutoBrightnessManager {

    private final Context context;
    private final SensorManager sensorManager;
    private final Sensor lightSensor;

    public AutoBrightnessManager(Context context) {
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    private final SensorEventListener lightSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                float lux = event.values[0];

                int brightness = luxToBrightness(lux);
                setScreenBrightness(brightness);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    private int luxToBrightness(float lux) {
        if (lux < 10)   return 30;
        if (lux < 50)   return 80;
        if (lux < 300)  return 140;
        if (lux < 1000) return 200;
        return 255;
    }

    private void setScreenBrightness(int brightness) {
        brightness = Math.max(20, Math.min(255, brightness));

        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            Window window = activity.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.screenBrightness = brightness / 255f;
            window.setAttributes(params);
        }
    }

    public void start() {
        if (lightSensor != null) {
            sensorManager.registerListener(lightSensorListener, lightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void stop() {
        sensorManager.unregisterListener(lightSensorListener);
    }
}