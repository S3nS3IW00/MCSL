package app.mcsl.events.listeners;

import app.mcsl.managers.file.DirectoryType;

public interface DirectoryChangeListener {

    void change(DirectoryType type);

}
