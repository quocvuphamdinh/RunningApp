package vu.pham.runningappseminar.database.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import vu.pham.runningappseminar.models.Run


@Dao
interface RunDAO {

    @Insert
    suspend fun insertRun(run: Run)

    @Delete
    suspend fun deleteRun(run: Run)

    @Query("SELECT * FROM running_table WHERE isRunWithExercise = 0 ORDER BY timestamp DESC")
    fun getAllRunsSortedByDate():LiveData<List<Run>>

    @Query("SELECT * FROM running_table WHERE isRunWithExercise = 0 ORDER BY timeInMillis DESC")
    fun getAllRunsSortedByTimeInMillies():LiveData<List<Run>>

    @Query("SELECT * FROM running_table WHERE isRunWithExercise = 0 ORDER BY averageSpeedInKilometersPerHour DESC")
    fun getAllRunsSortedByAvgSpeedInKMH():LiveData<List<Run>>

    @Query("SELECT * FROM running_table WHERE isRunWithExercise = 0 ORDER BY caloriesBurned DESC")
    fun getAllRunsSortedByCaloriesBurned():LiveData<List<Run>>

    @Query("SELECT * FROM running_table WHERE isRunWithExercise = 0 ORDER BY distanceInKilometers DESC")
    fun getAllRunsSortedByDistance():LiveData<List<Run>>

    @Query("SELECT * FROM running_table WHERE isRunWithExercise = 1 ORDER BY timestamp DESC")
    fun getAllRunsWithExerciseSortedByDate():LiveData<List<Run>>

    @Query("SELECT * FROM running_table WHERE isRunWithExercise = 1 ORDER BY timeInMillis DESC")
    fun getAllRunsWithExerciseSortedByTimeInMillies():LiveData<List<Run>>

    @Query("SELECT * FROM running_table WHERE isRunWithExercise = 1 ORDER BY averageSpeedInKilometersPerHour DESC")
    fun getAllRunsWithExerciseSortedByAvgSpeedInKMH():LiveData<List<Run>>

    @Query("SELECT * FROM running_table WHERE isRunWithExercise = 1 ORDER BY caloriesBurned DESC")
    fun getAllRunsWithExerciseSortedByCaloriesBurned():LiveData<List<Run>>

    @Query("SELECT * FROM running_table WHERE isRunWithExercise = 1 ORDER BY distanceInKilometers DESC")
    fun getAllRunsWithExerciseSortedByDistance():LiveData<List<Run>>

    @Query("SELECT SUM(timeInMillis) FROM running_table")
    fun getTotalTimeInMillies():LiveData<Long>

    @Query("SELECT AVG(averageSpeedInKilometersPerHour) FROM running_table")
    fun getTotalAvgSpeedInKMH():LiveData<Float>

    @Query("SELECT SUM(caloriesBurned) FROM running_table")
    fun getTotalCaloriesBurned():LiveData<Int>

    @Query("SELECT SUM(distanceInKilometers) FROM running_table")
    fun getTotalDistance():LiveData<Int>

    @Query("SELECT SUM(caloriesBurned) FROM running_table WHERE DATE(DATETIME(timestamp/1000, 'unixepoch')) = DATE('now')")
    fun getTotalCaloriesBurnedToDay():LiveData<Int>

    @Query("SELECT SUM(timeInMillis) FROM running_table WHERE DATE(DATETIME(timestamp/1000, 'unixepoch')) = DATE('now')")
    fun getTotalTimeInMillisToday():LiveData<Long>

    @Query("SELECT SUM(averageSpeedInKilometersPerHour) FROM running_table WHERE DATE(DATETIME(timestamp/1000, 'unixepoch')) = DATE('now')")
    fun getTotalAvgSpeedInKMHToday():LiveData<Float>

    @Query("SELECT COUNT(*) FROM running_table WHERE DATE(DATETIME(timestamp/1000, 'unixepoch')) = DATE('now')")
    fun getCountRunToday():LiveData<Int>

    @Query("SELECT MAX(distanceInKilometers) FROM running_table")
    fun getMaxDistance():LiveData<Int>

    @Query("SELECT MAX(timeInMillis) FROM running_table")
    fun getMaxTimeInMillies():LiveData<Long>

    @Query("SELECT MAX(caloriesBurned) FROM running_table")
    fun getMaxCaloriesBurned():LiveData<Int>

    @Query("SELECT MAX(averageSpeedInKilometersPerHour) FROM running_table")
    fun getMaxAvgSpeedInKMH():LiveData<Float>

    @Query("SELECT SUM(distanceInKilometers) FROM running_table WHERE strftime('%W',DATE(DATETIME(timestamp/1000, 'unixepoch'))) = strftime('%W',DATE('now'))")
    fun getTotalDitanceWeekly():LiveData<Int>

    @Query("SELECT * FROM running_table")
    suspend fun getAllRun():List<Run>

    @Query("DELETE FROM running_table")
    suspend fun deleteAllRun()

    @Query("SELECT * FROM running_table WHERE id = :id")
    fun getRunById(id:String) : LiveData<Run?>

    @Query("with \n" +
            " days as (\n" +
            "    select 1 nr, 'Monday' day union all\n" +
            "    select 2, 'Tuesday' union all\n" +
            "    select 3, 'Wednesday' union all\n" +
            "    select 4, 'Thursday' union all\n" +
            "    select 5, 'Friday' union all\n" +
            "    select 6, 'Saturday' union all\n" +
            "    select 7, 'Sunday'\n" +
            "  ),\n" +
            "  weekMonday as (\n" +
            "    select date(\n" +
            "        strftime('%Y-%m-%d', :date / 1000, 'unixepoch'), \n" +
            "        case when strftime('%w', strftime('%Y-%m-%d', :date / 1000, 'unixepoch')) <> '1' then '-7 day' else '0 day' end, \n" +
            "        'weekday 1'\n" +
            "      ) monday\n" +
            "  )\n" +
            "select coalesce(sum(caloriesBurned), 0)\n" +
            "from days d cross join weekMonday wm\n" +
            "left join running_table\n" +
            "on strftime('%w', strftime('%Y-%m-%d', timestamp / 1000, 'unixepoch')) + 0 = d.nr % 7\n" +
            "and date(strftime('%Y-%m-%d', timestamp / 1000, 'unixepoch')) between wm.monday and date(wm.monday, '6 day')\n" +
            "group by d.nr\n" +
            "order by d.nr")
    fun getTotalCaloriesBurnedInEachDay(date: Long): LiveData<List<Int>>

    @Query("with \n" +
            " days as (\n" +
            "    select 1 nr, 'Monday' day union all\n" +
            "    select 2, 'Tuesday' union all\n" +
            "    select 3, 'Wednesday' union all\n" +
            "    select 4, 'Thursday' union all\n" +
            "    select 5, 'Friday' union all\n" +
            "    select 6, 'Saturday' union all\n" +
            "    select 7, 'Sunday'\n" +
            "  ),\n" +
            "  weekMonday as (\n" +
            "    select date(\n" +
            "        strftime('%Y-%m-%d', :date / 1000, 'unixepoch'), \n" +
            "        case when strftime('%w', strftime('%Y-%m-%d', :date / 1000, 'unixepoch')) <> '1' then '-7 day' else '0 day' end, \n" +
            "        'weekday 1'\n" +
            "      ) monday\n" +
            "  )\n" +
            "select coalesce(sum(timeInMillis), 0)\n" +
            "from days d cross join weekMonday wm\n" +
            "left join running_table\n" +
            "on strftime('%w', strftime('%Y-%m-%d', timestamp / 1000, 'unixepoch')) + 0 = d.nr % 7\n" +
            "and date(strftime('%Y-%m-%d', timestamp / 1000, 'unixepoch')) between wm.monday and date(wm.monday, '6 day')\n" +
            "group by d.nr\n" +
            "order by d.nr")
    fun getTotalDurationInEachDay(date: Long): LiveData<List<Long>>

    @Query("with \n" +
            " days as (\n" +
            "    select 1 nr, 'Monday' day union all\n" +
            "    select 2, 'Tuesday' union all\n" +
            "    select 3, 'Wednesday' union all\n" +
            "    select 4, 'Thursday' union all\n" +
            "    select 5, 'Friday' union all\n" +
            "    select 6, 'Saturday' union all\n" +
            "    select 7, 'Sunday'\n" +
            "  ),\n" +
            "  weekMonday as (\n" +
            "    select date(\n" +
            "        strftime('%Y-%m-%d', :date / 1000, 'unixepoch'), \n" +
            "        case when strftime('%w', strftime('%Y-%m-%d', :date / 1000, 'unixepoch')) <> '1' then '-7 day' else '0 day' end, \n" +
            "        'weekday 1'\n" +
            "      ) monday\n" +
            "  )\n" +
            "select coalesce(sum(distanceInKilometers), 0)\n" +
            "from days d cross join weekMonday wm\n" +
            "left join running_table\n" +
            "on strftime('%w', strftime('%Y-%m-%d', timestamp / 1000, 'unixepoch')) + 0 = d.nr % 7\n" +
            "and date(strftime('%Y-%m-%d', timestamp / 1000, 'unixepoch')) between wm.monday and date(wm.monday, '6 day')\n" +
            "group by d.nr\n" +
            "order by d.nr")
    fun getTotalDistanceInEachDay(date: Long): LiveData<List<Int>>

    @Query("with \n" +
            " days as (\n" +
            "    select 1 nr, 'Monday' day union all\n" +
            "    select 2, 'Tuesday' union all\n" +
            "    select 3, 'Wednesday' union all\n" +
            "    select 4, 'Thursday' union all\n" +
            "    select 5, 'Friday' union all\n" +
            "    select 6, 'Saturday' union all\n" +
            "    select 7, 'Sunday'\n" +
            "  ),\n" +
            "  weekMonday as (\n" +
            "    select date(\n" +
            "        strftime('%Y-%m-%d', :date / 1000, 'unixepoch'), \n" +
            "        case when strftime('%w', strftime('%Y-%m-%d', :date / 1000, 'unixepoch')) <> '1' then '-7 day' else '0 day' end, \n" +
            "        'weekday 1'\n" +
            "      ) monday\n" +
            "  )\n" +
            "select coalesce(sum(averageSpeedInKilometersPerHour), 0)\n" +
            "from days d cross join weekMonday wm\n" +
            "left join running_table\n" +
            "on strftime('%w', strftime('%Y-%m-%d', timestamp / 1000, 'unixepoch')) + 0 = d.nr % 7\n" +
            "and date(strftime('%Y-%m-%d', timestamp / 1000, 'unixepoch')) between wm.monday and date(wm.monday, '6 day')\n" +
            "group by d.nr\n" +
            "order by d.nr")
    fun getTotalAvgSpeedInEachDay(date: Long): LiveData<List<Float>>

    @Query("with \n" +
            "  months as (\n" +
            "    select 1 nr, 'JAN' month union all\n" +
            "    select 2 nr, 'FEB' union all\n" +
            "    select 3 nr, 'MAR' union all\n" +
            "    select 4 nr, 'APR' union all\n" +
            "    select 5 nr, 'MAY' union all\n" +
            "    select 6 nr, 'JUN' union all\n" +
            "    select 7 nr, 'JUL' union all\n" +
            "    select 8 nr, 'AUG' union all\n" +
            "    select 9 nr, 'SEP' union all\n" +
            "    select 10 nr, 'OCT' union all\n" +
            "    select 11 nr, 'NOV' union all\n" +
            "    select 12 nr, 'DEC'\n" +
            "  )\n" +
            "select coalesce(sum(distanceInKilometers), 0)\n" +
            "from months m\n" +
            "left join running_table\n" +
            "on strftime('%m', strftime('%Y-%m-%d', timestamp / 1000, 'unixepoch')) + 0 = m.nr\n" +
            "and date(strftime('%Y-%m-%d', timestamp / 1000, 'unixepoch')) between date(strftime('%Y-%m-%d', :date / 1000, 'unixepoch'),'start of year') and date(strftime('%Y-%m-%d', :date / 1000, 'unixepoch'),'start of year', '1 year', '-1 day')\n" +
            "group by m.nr, m.month\n" +
            "order by m.nr")
    fun getTotalDistanceInEachMonth(date: Long): LiveData<List<Int>>

    @Query("with \n" +
            "  months as (\n" +
            "    select 1 nr, 'JAN' month union all\n" +
            "    select 2 nr, 'FEB' union all\n" +
            "    select 3 nr, 'MAR' union all\n" +
            "    select 4 nr, 'APR' union all\n" +
            "    select 5 nr, 'MAY' union all\n" +
            "    select 6 nr, 'JUN' union all\n" +
            "    select 7 nr, 'JUL' union all\n" +
            "    select 8 nr, 'AUG' union all\n" +
            "    select 9 nr, 'SEP' union all\n" +
            "    select 10 nr, 'OCT' union all\n" +
            "    select 11 nr, 'NOV' union all\n" +
            "    select 12 nr, 'DEC'\n" +
            "  )\n" +
            "select coalesce(sum(timeInMillis), 0)\n" +
            "from months m\n" +
            "left join running_table\n" +
            "on strftime('%m', strftime('%Y-%m-%d', timestamp / 1000, 'unixepoch')) + 0 = m.nr\n" +
            "and date(strftime('%Y-%m-%d', timestamp / 1000, 'unixepoch')) between date(strftime('%Y-%m-%d', :date / 1000, 'unixepoch'),'start of year') and date(strftime('%Y-%m-%d', :date / 1000, 'unixepoch'),'start of year', '1 year', '-1 day')\n" +
            "group by m.nr, m.month\n" +
            "order by m.nr")
    fun getTotalDurationInEachMonth(date: Long): LiveData<List<Long>>

    @Query("with \n" +
            "  months as (\n" +
            "    select 1 nr, 'JAN' month union all\n" +
            "    select 2 nr, 'FEB' union all\n" +
            "    select 3 nr, 'MAR' union all\n" +
            "    select 4 nr, 'APR' union all\n" +
            "    select 5 nr, 'MAY' union all\n" +
            "    select 6 nr, 'JUN' union all\n" +
            "    select 7 nr, 'JUL' union all\n" +
            "    select 8 nr, 'AUG' union all\n" +
            "    select 9 nr, 'SEP' union all\n" +
            "    select 10 nr, 'OCT' union all\n" +
            "    select 11 nr, 'NOV' union all\n" +
            "    select 12 nr, 'DEC'\n" +
            "  )\n" +
            "select coalesce(sum(caloriesBurned), 0)\n" +
            "from months m\n" +
            "left join running_table\n" +
            "on strftime('%m', strftime('%Y-%m-%d', timestamp / 1000, 'unixepoch')) + 0 = m.nr\n" +
            "and date(strftime('%Y-%m-%d', timestamp / 1000, 'unixepoch')) between date(strftime('%Y-%m-%d', :date / 1000, 'unixepoch'),'start of year') and date(strftime('%Y-%m-%d', :date / 1000, 'unixepoch'),'start of year', '1 year', '-1 day')\n" +
            "group by m.nr, m.month\n" +
            "order by m.nr")
    fun getTotalCaloriesBurnedInEachMonth(date: Long): LiveData<List<Int>>

    @Query("with \n" +
            "  months as (\n" +
            "    select 1 nr, 'JAN' month union all\n" +
            "    select 2 nr, 'FEB' union all\n" +
            "    select 3 nr, 'MAR' union all\n" +
            "    select 4 nr, 'APR' union all\n" +
            "    select 5 nr, 'MAY' union all\n" +
            "    select 6 nr, 'JUN' union all\n" +
            "    select 7 nr, 'JUL' union all\n" +
            "    select 8 nr, 'AUG' union all\n" +
            "    select 9 nr, 'SEP' union all\n" +
            "    select 10 nr, 'OCT' union all\n" +
            "    select 11 nr, 'NOV' union all\n" +
            "    select 12 nr, 'DEC'\n" +
            "  )\n" +
            "select coalesce(sum(averageSpeedInKilometersPerHour), 0)\n" +
            "from months m\n" +
            "left join running_table\n" +
            "on strftime('%m', strftime('%Y-%m-%d', timestamp / 1000, 'unixepoch')) + 0 = m.nr\n" +
            "and date(strftime('%Y-%m-%d', timestamp / 1000, 'unixepoch')) between date(strftime('%Y-%m-%d', :date / 1000, 'unixepoch'),'start of year') and date(strftime('%Y-%m-%d', :date / 1000, 'unixepoch'),'start of year', '1 year', '-1 day')\n" +
            "group by m.nr, m.month\n" +
            "order by m.nr")
    fun getTotalAvgSpeedInEachMonth(date: Long): LiveData<List<Float>>
}