package localdatabase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.provider.SyncStateContract;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;


@Entity
public class DailySteps {
    @PrimaryKey(autoGenerate = true)
    public int entryNumber;
    @ColumnInfo(name="user_id")
    public int userId;
    @ColumnInfo(name="steps_taken")
    public int stepTaken;
    @ColumnInfo(name="steps_date")
    public String stepDate;

    public DailySteps(int userId, int stepTaken, String stepDate) {
        //this.entryNumber = entryNumber;
        this.userId = userId;
        this.stepTaken = stepTaken;
        this.stepDate = stepDate;
    }

    public int getEntryNumber() {
        return entryNumber;
    }

    public void setEntryNumber(int entryNumber) {
        this.entryNumber = entryNumber;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getStepTaken() {
        return stepTaken;
    }

    public void setStepTaken(int stepTaken) {
        this.stepTaken = stepTaken;
    }

    public String getStepDate() {
        return stepDate;
    }

    public void setStepDate(String stepDate) {
        this.stepDate = stepDate;
    }
}


