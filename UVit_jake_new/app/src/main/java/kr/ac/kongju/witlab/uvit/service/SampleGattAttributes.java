/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.ac.kongju.witlab.uvit.service;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */


public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap<>();
    static {
        attributes.put("Entec LED Service", "0000FF00-0000-1000-8000-00805F9B34FB");
        attributes.put("Entec LED Write Characteristic", "0000FF01-0000-1000-8000-00805F9B34FB");
        attributes.put("Entec LED Read Characteristic", "0000FF02-0000-1000-8000-00805F9B34FB");

        attributes.put("Bluno nano Service", "0000DFB0-0000-1000-8000-00805F9B34FB");
        attributes.put("Bluno nano Write Characteristic", "0000DFB1-0000-1000-8000-00805F9B34FB");
        attributes.put("Bluno nano Read Characteristic", "0000DFB2-0000-1000-8000-00805F9B34FB");
    }

    public static String getUUID(String key) {
        return attributes.get(key);
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
