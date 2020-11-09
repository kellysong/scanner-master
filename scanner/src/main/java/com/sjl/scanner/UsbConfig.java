package com.sjl.scanner;

import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.sjl.scanner.util.ByteUtils;

/**
 * usb连接配置类
 *
 * @author Kelly
 * @version 1.0.0
 * @filename UsbConfig
 * @time 2020/7/1 16:55
 * @copyright(C) 2020 song
 */
public class UsbConfig {

    //usb连公共参数,
    // <p>USB Serial(RS232的常规COM 端口)  6790, 29987</p>
    // <p>USB hID 1550,2407</p>
    /**
     * 厂商 id
     */
    private int vendorId;
    /**
     * 产品 id
     */
    private int productId;

    // usb com 需要
    private SerialPortConfig serialPortConfig;

    public final static class SerialPortConfig {
        /**
         * 波特率
         */
        private int baudRate = 115200;
        /**
         * 数据位
         *
         * @param dataBits 默认8,可选值为5~8
         */
        private int dataBits = UsbSerialPort.DATABITS_8;
        /**
         * 校验位
         *
         * @param parity 0:无校验位(NONE，默认)；1:奇校验位(ODD);2:偶校验位(EVEN)
         */
        private int parity = UsbSerialPort.PARITY_NONE;
        /**
         * 停止位
         *
         * @param stopBits 默认1；1:1位停止位；2:2位停止位
         */
        private int stopBits = UsbSerialPort.STOPBITS_1;
        /**
         * 标志
         *
         * @param flags 默认0
         */
        private int flags = 0;

        public SerialPortConfig() {
        }

        /**
         * 波特率
         *
         * @param baudRate
         */
        public SerialPortConfig(int baudRate) {
            this.baudRate = baudRate;
        }

        public int getBaudRate() {
            return baudRate;
        }

        public void setBaudRate(int baudRate) {
            this.baudRate = baudRate;
        }

        public int getDataBits() {
            return dataBits;
        }

        public void setDataBits(int dataBits) {
            this.dataBits = dataBits;
        }

        public int getParity() {
            return parity;
        }

        public void setParity(int parity) {
            this.parity = parity;
        }

        public int getStopBits() {
            return stopBits;
        }

        public void setStopBits(int stopBits) {
            this.stopBits = stopBits;
        }

        public int getFlags() {
            return flags;
        }

        public void setFlags(int flags) {
            this.flags = flags;
        }
    }

    /**
     * usb cmd需要
     */
    private ScanCmd scanCmd = new ScanCmd();

    public static final class ScanCmd {
        /**
         * 扫码触发指令，打开
         */
        private byte[] scanOpen = ByteUtils.hexStringToByteArr("FF540D");
        /**
         * 扫码触发指令，关闭
         */
        private byte[] scanClose = ByteUtils.hexStringToByteArr("FF550D");

        public byte[] getScanOpen() {
            return scanOpen;
        }

        public void setScanOpen(byte[] scanOpen) {
            this.scanOpen = scanOpen;
        }

        public byte[] getScanClose() {
            return scanClose;
        }

        public void setScanClose(byte[] scanClose) {
            this.scanClose = scanClose;
        }
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public SerialPortConfig getSerialPortConfig() {
        return serialPortConfig;
    }

    public void setSerialPortConfig(SerialPortConfig serialPortConfig) {
        this.serialPortConfig = serialPortConfig;
    }

    public ScanCmd getScanCmd() {
        return scanCmd;
    }

    public void setScanCmd(ScanCmd scanCmd) {
        this.scanCmd = scanCmd;
    }
}

