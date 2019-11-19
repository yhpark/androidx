/*
 * Copyright 2019 The Android Open Source Project
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

package androidx.camera.camera2.impl;

import android.content.Context;

import androidx.camera.camera2.Camera2AppConfig;
import androidx.camera.core.AppConfig;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.UseCase;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.VideoCaptureConfig;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

/** JUnit test cases for UseCaseSurfaceOccupancyManager class. */
@SmallTest
@RunWith(AndroidJUnit4.class)
public final class UseCaseSurfaceOccupancyManagerTest {

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        AppConfig appConfig = Camera2AppConfig.create(context);
        CameraX.initialize(context, appConfig);
    }

    @After
    public void tearDown() throws ExecutionException, InterruptedException {
        CameraX.shutdown().get();
    }

    @Test(expected = IllegalArgumentException.class)
    public void failedWhenBindTooManyImageCapture() {
        ImageCapture.Builder builder = new ImageCapture.Builder();
        ImageCapture useCase1 = builder.build();
        ImageCapture useCase2 = builder.build();

        // Should throw IllegalArgumentException
        UseCaseSurfaceOccupancyManager.checkUseCaseLimitNotExceeded(
                Collections.<UseCase>singletonList(useCase1),
                Collections.<UseCase>singletonList(useCase2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void failedWhenBindTooManyVideoCapture() {
        VideoCaptureConfig config = new VideoCaptureConfig.Builder().getUseCaseConfig();
        VideoCapture useCase1 = new VideoCapture(config);
        VideoCapture useCase2 = new VideoCapture(config);

        // Should throw IllegalArgumentException
        UseCaseSurfaceOccupancyManager.checkUseCaseLimitNotExceeded(
                Collections.<UseCase>singletonList(useCase1),
                Collections.<UseCase>singletonList(useCase2));
    }

    @Test
    public void passWhenNotBindTooManyImageVideoCapture() {
        ImageCapture imageCapture = new ImageCapture.Builder().build();
        VideoCapture videoCapture = new VideoCaptureConfig.Builder().build();

        UseCaseSurfaceOccupancyManager.checkUseCaseLimitNotExceeded(
                Collections.<UseCase>singletonList(imageCapture),
                Collections.<UseCase>singletonList(videoCapture));
    }
}
