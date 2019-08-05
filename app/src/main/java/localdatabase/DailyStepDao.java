package localdatabase;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface DailyStepDao {
    @Query("SELECT * FROM DailySteps")
    List<DailySteps> getAll();

    @Insert
    long insert(DailySteps dailySteps);

    @Query("DELETE FROM DailySteps")
    void deleteAll();

    @Update(onConflict = REPLACE)
    public void updateSteps(DailySteps... dailySteps);

    @Query("SELECT * FROM DailySteps WHERE user_id = :userId and steps_date = :steps_date")
    DailySteps findByDateAndID(int userId, String steps_date);

    @Query("SELECT * FROM DailySteps WHERE entryNumber = :stepEntry")
    DailySteps findByStepNo(String stepEntry);

    @Query("SELECT COALESCE(sum(COALESCE(steps_taken,0)), 0) From DailySteps where user_id =:userId")
    LiveData<Double> getTotalSteps(int userId);
}
