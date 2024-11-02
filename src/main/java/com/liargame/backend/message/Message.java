package com.liargame.backend.message;

import java.io.Serializable;

public interface Message extends Serializable {
    String getType();
}
