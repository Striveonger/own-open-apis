{{- define "common.volumes" -}}
- name: app-logs
  hostPath:
    path: /var/logs/own/open/apis
    type: "DirectoryOrCreate"
- name: app-config
  configMap:
    name: {{ include "own-open-apis.name" . }}-app-config-map
{{- end -}}

{{- define "common.volumeMounts" -}}
- name: app-logs
  mountPath: {{.Values.app.log.path | default "/var/log/"}}
  readOnly: false
- name: app-config
  mountPath: /opt/app/configs
  readOnly: true
{{- end -}}