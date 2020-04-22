package com.privetalk.app.database.objects;

import java.util.List;

public class TempNotification {
        public List<NotificationObject> objects;
        public String[] ids;

        public TempNotification(List<NotificationObject> objects, String[] ids) {
            this.objects = objects;
            this.ids = ids;
        }
    }