package com.goryajnoff.todolist;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface NotesDao {
    @Query("SELECT * FROM notes")
    List<Note> getNotes();// LiveData для обновления данных
    // Single то же что и Completable только возвращает данные которые в скобках
    @Insert
    void add(Note note); //Completable укажет завершилаcь работа или нет
    @Query("DELETE FROM notes WHERE id =:id")
    void remove(int id);
}
