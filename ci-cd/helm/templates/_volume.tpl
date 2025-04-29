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
  mountPath: {{.Values.app.config.applicationYaml.logging.file.path | default "/var/log/"}}
  readOnly: false
- name: app-config
  mountPath: /opt/app/configs
  readOnly: true
{{- end -}}