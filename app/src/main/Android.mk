LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_PACKAGE_NAME := invoelfocus
LOCAL_CERTIFICATE := platform

LOCAL_RESOURCE_DIR += frameworks/support/v7/appcompat/res
LOCAL_RESOURCE_DIR += $(LOCAL_PATH)/res

src_dirs := java/
LOCAL_SRC_FILES := $(call all-java-files-under, $(src_dirs))
LOCAL_STATIC_JAVA_LIBRARIES +=  com.tv.serialport \
                                android-support-v7-appcompat
LOCAL_JNI_SHARED_LIBRARIES := libserialport

include $(BUILD_PACKAGE)




