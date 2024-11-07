package com.goryajnoff.todolist;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kotlin.jvm.Throws;

public class MainViewModel extends AndroidViewModel {
    // для взаимодействия между базой данных и активити по системе MVVM
    //он является посредником
    // мы должны общаться только чере LiveData
    private NotesDatabase notesDatabase;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MutableLiveData<List<Note>> notes = new MutableLiveData<>();
    //private int count;
    //private MutableLiveData<Integer> countLD = new MutableLiveData<>();// с помощью Mutable можем
    //создавать LD сами и устанавливать туда значения


    public MainViewModel(@NonNull Application application) {
        super(application);
        notesDatabase = NotesDatabase.getInstance(application);
    }

    public LiveData<List<Note>> getNotes() {
        // создаем объект LiveData чтобы из активити подписаться на него
        return notes;
    }
    public void refreshNotes(){
        Disposable disposable = getNotesRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Note>>() {
                    @Override
                    public void accept(List<Note> notesFromDb) throws Throwable {
                        notes.setValue(notesFromDb);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        Log.d("MainViewModel","Error refresh");
                    }
                });
        compositeDisposable.add(disposable);

    }
    private Single<List<Note>> getNotesRx(){
        return Single.fromCallable(new Callable<List<Note>>() {
            @Override
            public List<Note> call() throws Exception {
                return notesDatabase.notesDao().getNotes();
//                throw new Exception();
            }
        });
    }


//    public void showCount() {
//        count++;
//        countLD.setValue(count);
//    }

//    public LiveData<Integer> getCount() {
//        return countLD;
//    }

    public void remove(Note note) {
        Disposable disposable = removeRx(note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Throwable {
                        Log.d("MainViewModel", "subscribe");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        Log.d("MainViewModel","Error remove");
                    }
                });
        compositeDisposable.add(disposable);
    }
    private Completable removeRx(Note note){
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Throwable {
                notesDatabase.notesDao().remove(note.getId());
//                throw new Exception();
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
