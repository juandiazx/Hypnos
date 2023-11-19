#ifndef SENSOR_H
#define SENSOR_H

class Sensor {
public:
    int takeMeasurement();

protected:
    int sensorPin;
};

#endif