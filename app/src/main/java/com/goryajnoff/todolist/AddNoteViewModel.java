package com.goryajnoff.todolist;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AddNoteViewModel extends AndroidViewModel {
    private NotesDatabase notesDatabase;
    private MutableLiveData<Boolean> shouldCloseScreen = new MutableLiveData<>();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MutableLiveData<List<Note>> notes = new MutableLiveData<>();

    public AddNoteViewModel(@NonNull Application application) {
        super(application);
        notesDatabase = NotesDatabase.getInstance(application);
    }


    public void addNote(Note note) {
        Disposable disposable = addRx(note)
                //.delay(5, TimeUnit.SECONDS)// устанавливает задержку
                .subscribeOn(Schedulers.io())//подписка на поток запускае то что выше в другом потоке
                .observeOn(AndroidSchedulers.mainThread())// все что ниже будет на главном потоке
                .subscribe(new Action() {// подписка на метод без нее ничего не будет работать
                    @Override
                    public void run() throws Throwable {
                        //Log.d("AddNoteViewModel", "subscribe");
                        shouldCloseScreen.setValue(true);// вызываем  post так как его можно вызвать
                        //любого потока а не только из основного как set
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        Log.d("AddNoteViewModel", "Error add");
                    }
                });
        compositeDisposable.add(disposable);
    }

    private Completable addRx(Note note) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Throwable {
                notesDatabase.notesDao().add(note);
//                throw new Exception();
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();// с помощью compositeDisposable мы можем управлять сразу всеми
        // обьектами disposable которые добавили
        //disposable.dispose();// управляем подписками и отменяем их с помощбю disposable
    }

    public LiveData<Boolean> getShouldCloseScreen() {
        return shouldCloseScreen;
    }
}
