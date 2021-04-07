package com.example.fpptest;
import java.util.*;

public class FPPDataComparator implements Comparator<FPPData>{
    @Override
    public int compare(FPPData arg0, FPPData arg1) {
        return arg0.getIP().compareTo(arg1.getIP());
    }
}

