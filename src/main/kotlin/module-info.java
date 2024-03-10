module fun.irongate.storage {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;
    requires kotlinx.coroutines.core.jvm;


    opens fun.irongate.storage.controllers to javafx.fxml;
    opens fun.irongate.storage to javafx.fxml;
    exports fun.irongate.storage;
}