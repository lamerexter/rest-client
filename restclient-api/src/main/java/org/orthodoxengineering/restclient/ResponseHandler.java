package org.orthodoxengineering.restclient;

import org.beanplanet.core.net.http.HttpResponse;

public interface ResponseHandler<T> {
    T handleResponse(HttpResponse response);
}
