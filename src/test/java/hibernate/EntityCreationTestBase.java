package hibernate;

import com.google.inject.Inject;
import nl.tudelft.planningstool.database.DatabaseTestModule;
import nl.tudelft.planningstool.database.bootstrapper.BootstrapRule;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

@RunWith(JukitoRunner.class)
@UseModules(DatabaseTestModule.class)
public class EntityCreationTestBase {

    @Rule
    @Inject
    public BootstrapRule bootstrapRule;

    @Rule
    public ExpectedException expected = ExpectedException.none();
}
