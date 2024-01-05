/*
 * Copyright © 2024 fluffydaddy
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

package io.fluffydaddy.jtasks.extensions.progress;

import io.fluffydaddy.jtasks.core.ITaskService;
import io.fluffydaddy.jtasks.impl.TaskTracker;
import io.fluffydaddy.jutils.format.MessageFormatter;

import java.util.List;

public class ProgressTracker extends TaskTracker {
    private static ProgressListener generateInformation(final long totalAmount) {
        return new ProgressListener() {
            @Override
            public void onProgressChanged(double progressAmount, long amountLength, double speedAmount, double readedLength) {
                try {
                    // speed indicator in KB/s
                    String totalStr = MessageFormatter.formatSize(totalAmount, false);
                    
                    String speedStr = MessageFormatter.formatSize(speedAmount, false);
                    String limitStr = MessageFormatter.formatSize(amountLength, false);
                    String countStr = MessageFormatter.formatSize(readedLength, false);
                    
                    String receiveStr = String.format("%.2f", progressAmount) + "%";
                    
                    String fmt = "[Speed %s • Receive %s] / [Progress %s • Readed %s / Total %s]";
                    Object[] args = {speedStr, limitStr, receiveStr, countStr, totalStr};
                    String msg = String.format(fmt, args);
                    
                    System.out.println(msg);
                } catch (ArithmeticException e) {
                    System.out.println("Arithmetic Error! - " + e);
                }
            }
        };
    }
    
    // Количество всех данных
    private long totalAmount;
    // Количество данных прогресса
    private long progressAmount;
    // Количество элементов на просчитывание.
    private int amountProgressItems;
    // Процент всего количества данных
    private double progressBarPercentage;
    
    // За какой промежуток времени вычисленно прогресса
    private double speedBarAmount;
    // Количество просчитанного прогресса
    private double totalBarAmount;
    
    private long startingCalcTime;
    private long finishedCalcTime;
    
    // Сама задача, которую хотим отследить.
    private ProgressTask _trackProgressive;
    // Слушатель из вне задачи.
    private ProgressListener _trackedBar;
    
    public ProgressTracker(String tag) {
        super(tag);
    }
    
    // Отслеживаем задачу...
    public void track(ProgressTask task) {
        super.track(task);
        
        _trackProgressive = task;
        _trackProgressive.submit(this);
    }
    
    public void untrack(ProgressTask task) {
        super.untrack(task);
        
        _trackProgressive = task;
        _trackProgressive.cancel(this);
    }
    
    public long getStartTime() {
        return startingCalcTime;
    }
    
    public long getFinishTime() {
        return finishedCalcTime;
    }
    
    public void startTracking() {
        if (_trackProgressive != null) {
            startingCalcTime = System.currentTimeMillis();
        }
        
        super.startTracking();
    }
    
    // Останавливает слежку за задачей.
    public List<ITaskService> stopTracking() {
        if (_trackProgressive != null) {
            finishedCalcTime = System.currentTimeMillis();
        }
        
        clear();
        
        return super.stopTracking();
    }
    
    public void setListener(ProgressListener listener) {
        _trackedBar = listener;
    }
    
    //returns if the progress bar needs to change
    public boolean progress(long amount) {
        progressAmount += amount;
        
        boolean result = true;
        
        result = result && updateProgressbarPercentage();
        result = result && updateSpeedbar();
        result = result && updateTotalbar();
        result = result && _trackedBar != null;
        
        if (result) {
            _trackedBar.onProgressChanged(progressBarPercentage, amount, getSpeed(), getTotal());
        }
        
        return result;
    }
    
    public double getTotal() {
        return totalBarAmount;
    }
    
    public double getProgress() {
        return progressBarPercentage;
    }
    
    public double getSpeed() {
        return speedBarAmount;
    }
    
    public boolean setProgressItem(long itemAmount) {
        clear();
        return addProgressItem(itemAmount);
    }
    
    //returns if the progressbar needs to change
    public boolean addProgressItem(long itemAmount) {
        totalAmount = totalAmount + itemAmount;
        amountProgressItems++;
        
        return updateProgressbarPercentage();
    }
    
    public int getAmountOfItems() {
        return amountProgressItems;
    }
    
    public boolean removeProgressItem(long byteAmount) {
        if (amountProgressItems > 0) {
            totalAmount = totalAmount - byteAmount;
            amountProgressItems--;
        }
        
        return updateProgressbarPercentage();
    }
    
    public void clear() {
        totalAmount = 0;
        progressAmount = 0;
        amountProgressItems = 0;
        progressBarPercentage = 0;
        startingCalcTime = 0;
        speedBarAmount = 0;
        totalBarAmount = 0;
    }
    
    //returns true if has been updated;
    private boolean updateProgressbarPercentage() {
        double newProgressBarPercentage = calculateProgressPercentage();
        
        if (progressBarPercentage == newProgressBarPercentage) {
            return false;
        } else {
            progressBarPercentage = newProgressBarPercentage;
            return true;
        }
    }
    
    private boolean updateSpeedbar() {
        double newSpeedBar = calculateProgressSpeed();
        
        if (speedBarAmount == newSpeedBar) {
            return false;
        } else {
            speedBarAmount = newSpeedBar;
            return true;
        }
    }
    
    private boolean updateTotalbar() {
        double newTotalBar = calculateTotalPerBytes();
        
        if (totalBarAmount == newTotalBar) {
            return false;
        } else {
            totalBarAmount = newTotalBar;
            return true;
        }
    }
    
    private double calculateProgressPercentage() {
        return ((double) progressAmount / (double) totalAmount) * 100.0;
    }
    
    private double calculateProgressSpeed() {
        double estimateTime = (System.currentTimeMillis() - startingCalcTime) / 1000;
        return calculateProgressSpeed(estimateTime);
    }
    
    private double calculateTotalPerBytes() {
        double progressDecimal = progressBarPercentage / 100;
        double bytesReaded = progressDecimal * totalAmount;
        
        return bytesReaded;
    }
    
    private double calculateProgressSpeed(double estimateTime) {
        if (estimateTime <= 0) {
            return 0.0D;
        }
        
        double bytesPerSec = progressAmount / estimateTime;
        return bytesPerSec;
    }
}
