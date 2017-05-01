package io.github.dmi3coder.aspectissions;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
public class PermissionAspect {

  @Around("execution(@DangerousPermission * *(..))")
  public Object beforeDangerousMethod(ProceedingJoinPoint point)
      throws Throwable {
    Activity activity = ((Activity) point.getThis());
    DangerousPermission dangerousPermission =
        ((MethodSignature) point.getSignature()).getMethod()
            .getAnnotation(DangerousPermission.class);
    String requiredPermission = dangerousPermission.value();
    if (VERSION.SDK_INT >= VERSION_CODES.M) {
      if (activity.checkSelfPermission(requiredPermission) == PackageManager.PERMISSION_GRANTED) {
        point.proceed();
        return point;
      } else {
        activity.requestPermissions(new String[]{requiredPermission}, 1);
        return point;
      }
    }
    point.proceed();
    return point;
  }
}

