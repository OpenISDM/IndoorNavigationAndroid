package com.example.android.waypointbasedindoornavigation;


public class UUIDtoID {
    public String trUUID (String s){
        s = ptr(s);
        return s;
    }
    String ptr(String s) {
        switch (s) {
            case "0x517008d90x0001ee7f":
                return "TEST01";
            case "0xf853bd410xfe54f142":
                return "C01";
            case "0x6029b8410x580af042":
                return "C02";
            case "0xf953bd410xfe54f142":
                return "C03";
            case "0xfa53bd410xfe54f142":
                return "C04";
            case "0xfa53bd410xff54f142":
                return "C05";
            case "0xfb53bd410xff54f142":
                return "C06";
            case "0xcf90b8410x3424f042":
                return "C07";
            case "0x2ebab8410x8c2ef042":
                return "C09";
            case "0xdeceb8410xb833f042":
                return "C10";
            case "0xfe53bd410xff54f142":
                return "C11";
            case "0x3ef8b8410x0f3ef042":
                return "C12";
            case "0xee0cb9410x3b43f042":
                return "C13";
            case "0xff53bd410x0055f142":
                return "C14";
            case "0x4d36b9410x934df042":
                return "C15";
            case "0x0054bd410x0055f142":
                return "C16";
            case "0x0154bd410x0055f142":
                return "C17";
            case "0x0254bd410x0055f142":
                return "C19";
            case "0x0254bd410x0155f142":
                return "C20";
            case "0xed4cc8410x0e21f342":
                return "C21";
            case "0x1cc7b9410xc771f042":
                return "C22";
            case "0x0454bd410x0155f142":
                return "C23";
            case "0x7cf0b9410x1f7cf042":
                return "C24";
            case "0x2c05ba410x4b81f042":
                return "C25";
            case "0xdc19ba410x7786f042":
                return "C26";
            case "0x8b2eba410xa38bf042":
                return "C27";
            case "0x3b43ba410xcf90f042":
                return "C28";
            case "0x4993bd410x640df142":
                return "A04";
            case "0x6793bd410x5d0df142":
                return "A05";
            case "0x4b81ca410x0e21f342":
                return "C31";
            default:
                return s;
        }
    }
}
