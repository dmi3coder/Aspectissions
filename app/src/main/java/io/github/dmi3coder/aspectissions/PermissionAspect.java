package io.github.dmi3coder.aspectissions;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
public class PermissionAspect {

  @Around("execution(@DangerousPermission void *(..))")
  public void beforeDangerousMethod(ProceedingJoinPoint point)
      throws Throwable {
    Activity activity = ((Activity) point.getThis());
    MethodSignature signature = (MethodSignature) point.getSignature();
    Method method = signature.getMethod();

    DangerousPermission dangerousPermission = method.getAnnotation(DangerousPermission.class);

    String requiredPermission = dangerousPermission.value();
    if (VERSION.SDK_INT >= VERSION_CODES.M) {
      if (activity.checkSelfPermission(requiredPermission) != PackageManager.PERMISSION_GRANTED) {
        activity.requestPermissions(new String[]{requiredPermission}, 1);
        return;
      }
    }
    point.proceed();
  }
}

