CREATE DATABASE IF NOT EXISTS aerosense;

USE aerosense;

CREATE TABLE User (
    userID INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME
);

CREATE TABLE Hub (
    hubID INT AUTO_INCREMENT PRIMARY KEY,
    modelNumber VARCHAR(255) NOT NULL,
    userID INT NOT NULL,
    batteryLevel INT NOT NULL,
    FOREIGN KEY (userID) REFERENCES User(userID)
);

CREATE TABLE Location (
    locationID INT AUTO_INCREMENT PRIMARY KEY,
    hubID INT NOT NULL,
    latitude DECIMAL(9,6) NOT NULL,
    longitude DECIMAL(9,6) NOT NULL,
    createdAt DATETIME NOT NULL,
    FOREIGN KEY (hubID) REFERENCES Hub(hubID)
);

CREATE TABLE AirQualityMeasurement (
    measurementID INT AUTO_INCREMENT PRIMARY KEY,
    hubID INT NOT NULL,
    PM1 DECIMAL(10,2),
    PM2_5 DECIMAL(10,2),
    PM10 DECIMAL(10,2),
    VOC DECIMAL(10,2),
    temperature DECIMAL(5,2),
    humidity DECIMAL(5,2),
    gas_resistance DECIMAL(10,2),
    pollenCount INT,
    timestamp DATETIME NOT NULL,
    FOREIGN KEY (hubID) REFERENCES Hub(hubID)
);

CREATE TABLE Notification (
    notificationID INT AUTO_INCREMENT PRIMARY KEY,
    userID INT NOT NULL,
    message TEXT NOT NULL,
    isRead BOOLEAN NOT NULL DEFAULT FALSE,
    createdAt DATETIME NOT NULL,
    FOREIGN KEY (userID) REFERENCES User(userID)
);

CREATE TABLE AsthmaProfile (
    profileID INT AUTO_INCREMENT PRIMARY KEY,
    userID INT NOT NULL,
    personalTrigger VARCHAR(255),
    asthmaCondition VARCHAR(255),
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME,
    FOREIGN KEY (userID) REFERENCES User(userID)
);

CREATE TABLE UserSetting (
    settingID INT AUTO_INCREMENT PRIMARY KEY,
    userID INT NOT NULL,
    notificationFrequency INT,
    vibration BOOLEAN NOT NULL,
    sound BOOLEAN NOT NULL,
    FOREIGN KEY (userID) REFERENCES User(userID)
);

-- Indexes for faster querying jut for future reference
CREATE INDEX idx_user_email ON User(email);
CREATE INDEX idx_hub_user ON Hub(userID);
CREATE INDEX idx_location_hub ON Location(hubID);
CREATE INDEX idx_measurement_hub ON AirQualityMeasurement(hubID);
CREATE INDEX idx_notification_user ON Notification(userID);
CREATE INDEX idx_asthma_user ON AsthmaProfile(userID);
CREATE INDEX idx_setting_user ON UserSetting(userID);
