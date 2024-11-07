package com.goryajnoff.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerViewNotes;
    private FloatingActionButton ButtonAddNote;
    private NotesAdapter notesAdapter;
    private MainViewModel mainViewModel;

    private Handler handler = new Handler(Looper.getMainLooper());// передаем ссылку на основной
    //поток. Запуск Handler происходит в другом потоке но код в нем запускается в основном
    // занимаемся всем этим потому что с view элементами можем работать только в основном потоке

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);//для того чтобы
        //вью модель жила долго и не разрушалась при переворотах экрана
//        mainViewModel.getCount().observe(this, new Observer<Integer>() {
//            @Override
//            public void onChanged(Integer count) {  //с помощью этого метода реагируем на изменения
//                Toast.makeText(MainActivity.this,String.valueOf(count),Toast.LENGTH_SHORT)
//                        .show();
//            }
//        });
        notesAdapter = new NotesAdapter();


//        notesAdapter.setOnNoteClickListener(new NotesAdapter.OnNoteClickListener() {
//            @Override
//            public void onNoteClick(Note note) {
////                notesDatabase.notesDao().remove(note.getId());
////                showNotes
//                mainViewModel.showCount();
//            }
//        });


        recyclerViewNotes.setAdapter(notesAdapter);


        mainViewModel.getNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                notesAdapter.setNotes(notes);
            }
        });//мы подписыавемся на LiveData и в переопределяем метод. благодаря LiveData нам не нужен
        // showNotes так как она автоматом обновляется при любых изменениях а также не нужен handler
        //и запуск в другом  потоке так как LiveData сама  будет  всё переключать


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback
                (0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Note note = notesAdapter.getNotes().get(position);
                mainViewModel.remove(note);
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerViewNotes);



        ButtonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchNewScreen();
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        mainViewModel.refreshNotes();
        //showNotes();
    }



    private void initViews() {
        recyclerViewNotes = findViewById(R.id.recyclerViewNotes);
        ButtonAddNote = findViewById(R.id.ButtonAddNote);
    }

//    private void showNotes() {
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                List<Note> notesList = notesDatabase.notesDao().getNotes();
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        notesAdapter.setNotes(notesList);
//                    }
//                });
//            }
//        });
//        thread.start();
//    }



    private void launchNewScreen() {
        Intent intent = AddNoteActivity.newIntent(this);
        startActivity(intent);
    }
}