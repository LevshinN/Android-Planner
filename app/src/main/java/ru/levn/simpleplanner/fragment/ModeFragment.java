package ru.levn.simpleplanner.fragment;

import android.support.v4.app.Fragment;

import java.lang.reflect.Field;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 21.08.2015.
 */
public abstract class ModeFragment extends Fragment implements ModeFragmentInterface {

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
