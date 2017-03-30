package com.zenkun.estimote.action;

import rx.Observable;

public interface Action<ReturnType> {

  Observable<ReturnType> observable();
}