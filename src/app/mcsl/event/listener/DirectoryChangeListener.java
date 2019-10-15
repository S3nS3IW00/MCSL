package app.mcsl.event.listener;

import app.mcsl.manager.file.DirectoryType;

public interface DirectoryChangeListener {

    void change(DirectoryType type);

}
