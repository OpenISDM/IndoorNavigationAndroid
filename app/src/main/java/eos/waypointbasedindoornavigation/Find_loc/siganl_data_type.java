package eos.waypointbasedindoornavigation.Find_loc;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class siganl_data_type implements Comparable<siganl_data_type>{
    private int sort_ways = 0;
    private List<Integer> rssi = new ArrayList<>();
    private List<Integer> rssi_delete_extreme_value = new ArrayList<>();
    private String uuid = null;
    private int parameter;
    public siganl_data_type(String s, int i){
        uuid = s;
        rssi.add(i);
    }
    public siganl_data_type(String s, int i, int j){
        uuid = s;
        rssi.add(i);
        parameter = j;
    }
    public void setvalue(String s){
        uuid = s;
    }
    public void setvalue(int i){
        List<Integer> tmp_list = new ArrayList<>();
        tmp_list.add(i);
        tmp_list.addAll(rssi);
        rssi.clear();
        rssi.addAll(tmp_list);
        tmp_list.clear();
        setvalue_extreme_value();
    }

    public void setvalue_extreme_value(){
        List<Integer> tmp_list = new ArrayList<>();
        tmp_list.addAll(rssi);
        Collections.sort(tmp_list);
        tmp_list.remove(0);
        tmp_list.remove((tmp_list.size()-1));
        rssi_delete_extreme_value.clear();
        rssi_delete_extreme_value.addAll(tmp_list);
        tmp_list.clear();
    }
    public String getUuid(){
        return uuid;
    }

    public String getUuid_Name(){
      String Name = null;

      switch (uuid){
          case "0xf853bd410xfe54f142":
              return  "1~10診走廊出口(A1)";
          case "0x6029b8410x580af042":
              return "心臟內科/內科/體檢區(A2)";
          case "0x0f3eb8410x3921f342":
              return "心臟內科/內科/體檢區(A3)";
          case "0xbf52c8410x3323f542":
              return "服務台(A4)";
          case "0xeb57ca410x0e21f342":
              return "服務台(A5)";
          case "0xfa53bd410xff54f142":
              return "前門出口(A6)";
          case "0xeaaac2410xc1a9f042":
              return "前門出口(A7)";
          case "0xcf90b8410x3424f042":
              return "批價櫃檯(A11)";
          case "0x0400c8410x0721f342":
              return "前門出口(A9)";
          case "0x2ebab8410x8c2ef042":
              return "精神科/神經內科(A12)";
          case "0xdeceb8410xb833f042":
              return "26~29診走廊出口(A13)";
          case "0x4b81ca410x0e21f342":
              return "中央走廊(A8)";
          case "0x12f6af410x5442f242":
              return "自動繳費機(A10)";
          case "0x3ef8b8410x0f3ef042":
              return "小兒科(A14)";
          case "0xee0cb9410x3b43f042":
              return "小兒科(A15)";
          case "0xff53bd410x0055f142":
              return "健康教育中心(A16)";
          case "0xbb3fc8410x0721f342":
              return "健康教育中心(A17)";
          case "0x4d36b9410x934df042":
              return "新大樓一樓樓梯(A18)";
          case "0x0154bd410x0055f142":
              return "30~41診走廊交叉口(A19)";
          case "0x0254bd410x0055f142":
              return "外科/骨科/牙科(A20)";
          case "0x0254bd410x0155f142":
              return "外科/骨科/牙科(A21)";
          case "0xed4cc8410x0e21f342":
              return "腎臟科/腎膽腸內科/新陳代謝分泌科(A22)";
          case "0x1cc7b9410xc771f042":
              return "無障礙領藥窗口(A23)";
          case "0x0454bd410x0155f142":
              return "志工服務台(A25)";
          case "0x7cf0b9410x1f7cf042":
              return "42~49診走廊交叉口(A26)";
          case "0x2c05ba410x4b81f042":
              return "眼科/皮膚科(A27)";
          case "0xdc19ba410x7786f042":
              return "眼科/皮膚科(A28)";
          case "0x8b2eba410xa38bf042":
              return "42~49診走廊出口(A29)";
          case "0x0054bd410x0055f142":
              return "藥局(A24)";
          case "0x3b43ba410xcf90f042":
              return "藥局(裡面)(A30)";
          case "0x4993bd410x640df142":
              return "超商左(A31)";
          case "0x6793bd410x5d0df142":
              return "超商右(A32)";
          case "0x283ff0420x00000000":
              return "新舊大樓連接走廊(C1)";
          case "0xf295c2410x63a8f042":
              return "樓梯(舊大樓靠新大樓)(C2)";
          case "0x8193bd410x540df142":
              return "樓梯(舊大樓靠電梯)(C3)";
          case "0xc43af3420x00000000":
              return "核子醫學部(C4)";
          case "0xde57c8410x0721f342":
              return "抽血(C5)";
          case "0x0800b8410x0200f042":
              return "大廳(病歷室前)(C8)";
          case "0x3519b8410x4d06f042":
              return "大廳(電腦斷層室前)(C11)";
          //case "0x300000000x48d2b060":
              //return "後門出口(前)(C9)";
          case "0xeb57ba410xfb95f042":
              return "後門出口(後)(C10)";
          case "0x3319b8410x4d06f042":
              return "樓梯(舊大樓2F電梯旁)(D1)";
          case "0x3219b8410x4d06f042":
              return "神經部檢查室/心臟血管功能檢查室岔路(D4)";
          case "0x021234110x00020000":
              return "神經部檢查室(D2)";
          case "0x553bc2410x2d44f042":
              return "迴轉樓梯(神經部前)(D3)";
          case "0x006317170x00020000":
              return "心臟血管功能檢查室(D5)";
          case "0xa37dbe410x44d4f042":
              return "迴轉樓梯(心臟血管功能檢查室)(D6)";
          case "0x0193bd410x780df142":
              return "樓梯(新大樓B1)(B1)";
          case "0x5c93bd410x4f0df142":
              return "X光報到處(B3)";
          case "0x5519b8410x5506f042":
              return "電梯(B2)";
          default:
              return uuid;
      }

    }

    public int getrssi(int i){
        return rssi.get(i);
    }
    public String getrssilist(){
        return rssi.toString();
    }
    public String getrssilist_delete_extremevalue(){
        return rssi_delete_extreme_value.toString();
    }
    public String getrssilistsize(){
        return String.valueOf(rssi.size());
    }
    public int getrssi(){
        return rssi.get(rssi.size()-1);
    }
    public int countsum(){
        int count=0;
        for (int i:rssi){
            count += i;
        }
        return count;
    }
    public float countavg(){
        float count=0,num=0;
        for (int i:rssi){
           count += i;
           num ++;
        }
        return (count/num);
    }

    public float getmiddlenum(){
        int middlenum = 0, middlesize = 0;
        List<Integer> tmp_list = new ArrayList<>();
        tmp_list.clear();
        tmp_list.addAll(rssi);
        Collections.sort(tmp_list);
        middlesize = tmp_list.size()/2;
        middlenum = tmp_list.get(middlesize);
        return  middlenum;
    }

    public float countStandard_Deviation(){
        float count=0,num=0,avg=0,standard=0,base=0;
        for (int i:rssi){
            count += i;
            num ++;
        }
        avg = count/num;
        for (int i:rssi){
            base = (float) Math.pow(i-avg,2);
            standard = standard + base;
        }
        standard = (float) Math.sqrt((standard/num));

        return standard;
    }


    public void set_sort_way(int sort_way){
        this.sort_ways = sort_way;
    }



    @Override
    public int compareTo(@NonNull siganl_data_type f) {
        switch (sort_ways) {
            case 1:
                if (this.countavg() < f.countavg()) {
                    return 1;
                } else if (this.countavg() > f.countavg()) {
                    return -1;
                } else {
                    return 0;
                }
            default:
                if (this.countsum() < f.countsum()) {
                    return 1;
                } else if (this.countsum() > f.countsum()) {
                    return -1;
                } else {
                    return 0;
                }
        }
    }
}
