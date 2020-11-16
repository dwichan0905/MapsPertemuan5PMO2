package com.dwichan.uts.maps.model;

import java.util.List;

public interface DirectionFindList {
    void onDirectionFindStart();

    void onDirectionFindSuccess(List<Route> routes);
}
